package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.screen.AutoCrafterScreenHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class AutoCrafterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory {

    private final DefaultedList<ItemStack> recipeInventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> internalInventory = DefaultedList.ofSize(10, ItemStack.EMPTY); // 0..8 in, 9 out

    private static final int[] INPUT_SLOTS = IntStream.range(0, 9).toArray();
    private static final int[] OUTPUT_SLOT = new int[]{9};

    private CraftingRecipe cachedRecipe = null;
    private boolean isRecipeGridDirty = true;
    private int craftCooldown = 0;

    // Transient crafting grid for matching/lookup
    private final CraftingInventory craftingInventory = new CraftingInventory(new DummyHandler(), 3, 3);

    public AutoCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUTO_CRAFTER_BLOCK_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Auto Crafter");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AutoCrafterScreenHandler(ModScreenHandlers.AUTO_CRAFTER_SCREEN_HANDLER, syncId, playerInventory, this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0 &&
                world != null && world.getBlockEntity(pos) == this;
    }

    /** Ticking **/
    public static void tick(World world, BlockPos pos, BlockState state, AutoCrafterBlockEntity be) {
        if (world.isClient || be.isRecipeEmpty()) return;

        if (be.craftCooldown > 0) {
            be.craftCooldown--;
            return;
        }
        be.craftCooldown = 20;

        // Refresh recipe if the visible 3x3 changed
        if (be.isRecipeGridDirty) {
            be.syncCraftingInventoryFromRecipeGrid();
            RecipeManager recipeManager = ((ServerWorld) world).getServer().getRecipeManager();
            be.cachedRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, be.craftingInventory, world).orElse(null);
            be.isRecipeGridDirty = false;
        }
        if (be.cachedRecipe == null) return;

        // Eject trash (anything not matching any ingredient of the cached recipe)
        be.dropNonMatchingItems(world, pos, be.cachedRecipe);

        // If output cannot accept, stop early
        ItemStack preview = be.cachedRecipe.craft(be.craftingInventory, world.getRegistryManager());
        if (preview.isEmpty()) return;

        ItemStack outputStack = be.internalInventory.get(9);
        boolean canInsertOutput = outputStack.isEmpty()
                || (ItemStack.canCombine(outputStack, preview)
                && outputStack.getCount() + preview.getCount() <= Math.min(preview.getMaxCount(), be.getMaxCountPerStack()));
        if (!canInsertOutput) return;

        // Build a consumption plan (shaped to the recipe’s 3x3), ensuring we truly have the ingredients.
        int[] plan = be.buildConsumptionPlan(be.cachedRecipe);
        if (plan == null) return; // missing ingredients

        // Consume according to plan while building a transient grid for remainder calculation
        CraftingInventory consumedGrid = new CraftingInventory(new DummyHandler(), 3, 3);
        be.consumeAccordingToPlanAndFillGrid(plan, consumedGrid);

        // Handle output
        if (outputStack.isEmpty()) {
            be.internalInventory.set(9, preview.copy());
        } else {
            outputStack.increment(preview.getCount());
        }

        // Handle container items / remainders faithfully
        DefaultedList<ItemStack> remainders = be.cachedRecipe.getRemainder(consumedGrid);
        for (int i = 0; i < remainders.size(); i++) {
            ItemStack rem = remainders.get(i);
            if (rem.isEmpty()) continue;
            if (!be.tryInsertIntoInputs(rem)) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), rem);
            }
        }

        be.markDirty();
    }

    /** Build a shaped consumption plan mapping each recipe 3x3 index -> internal input slot index, or -1 if empty ingredient. */
    private int[] buildConsumptionPlan(CraftingRecipe recipe) {
        int[] plan = new int[9];
        for (int i = 0; i < 9; i++) plan[i] = -1;

        DefaultedList<Ingredient> ings = recipe.getIngredients();
        // Work on a mutable copy of input counts so we don’t mutate real inventory unless we succeed
        int[] counts = new int[9];
        for (int s = 0; s < 9; s++) counts[s] = internalInventory.get(s).getCount();

        for (int i = 0; i < 9; i++) {
            Ingredient ing = i < ings.size() ? ings.get(i) : Ingredient.EMPTY;
            if (ing.isEmpty()) continue;

            // Find any internal slot that matches this ingredient and still has count > 0
            boolean matched = false;
            for (int s = 0; s < 9; s++) {
                ItemStack stack = internalInventory.get(s);
                if (counts[s] > 0 && ing.test(stack)) {
                    plan[i] = s;
                    counts[s]--; // Reserve one
                    matched = true;
                    break;
                }
            }
            if (!matched) return null; // missing something; abort
        }
        return plan;
    }

    /** Apply the plan: decrement inputs and populate a transient 3×3 grid with single-count copies for remainder logic. */
    private void consumeAccordingToPlanAndFillGrid(int[] plan, CraftingInventory gridOut) {
        for (int i = 0; i < 9; i++) {
            int src = plan[i];
            if (src < 0) {
                gridOut.setStack(i, ItemStack.EMPTY);
                continue;
            }
            ItemStack srcStack = internalInventory.get(src);
            // Place a single-copy into the transient grid
            ItemStack one = srcStack.copy();
            one.setCount(1);
            gridOut.setStack(i, one);

            // Decrement the actual input by one
            srcStack.decrement(1);
            if (srcStack.isEmpty()) {
                internalInventory.set(src, ItemStack.EMPTY);
            }
        }
    }

    /** Attempt to insert a remainder back into the 0..8 inputs, preferring merges before empty slots. */
    private boolean tryInsertIntoInputs(ItemStack stack) {
        if (stack.isEmpty()) return true;

        // Try merges first
        for (int i = 0; i < 9; i++) {
            ItemStack cur = internalInventory.get(i);
            if (cur.isEmpty()) continue;
            if (ItemStack.canCombine(cur, stack)) {
                int max = Math.min(cur.getMaxCount(), getMaxCountPerStack());
                int space = max - cur.getCount();
                if (space <= 0) continue;
                int toMove = Math.min(space, stack.getCount());
                if (toMove > 0) {
                    cur.increment(toMove);
                    stack.decrement(toMove);
                    if (stack.isEmpty()) return true;
                }
            }
        }
        // Then empty slots
        for (int i = 0; i < 9; i++) {
            ItemStack cur = internalInventory.get(i);
            if (cur.isEmpty()) {
                int max = Math.min(stack.getMaxCount(), getMaxCountPerStack());
                ItemStack placed = stack.copy();
                placed.setCount(Math.min(stack.getCount(), max));
                internalInventory.set(i, placed);
                stack.decrement(placed.getCount());
                if (stack.isEmpty()) return true;
            }
        }
        return stack.isEmpty();
    }

    /** Eject anything in inputs that cannot serve the current recipe. */
    private void dropNonMatchingItems(World world, BlockPos pos, CraftingRecipe recipe) {
        Set<Item> validItems = computeValidItemsForRecipe(recipe);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = internalInventory.get(i);
            if (stack.isEmpty()) continue;
            if (!validItems.isEmpty() && !validItems.contains(stack.getItem())) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack);
                internalInventory.set(i, ItemStack.EMPTY);
            }
        }
    }

    private Set<Item> computeValidItemsForRecipe(CraftingRecipe recipe) {
        Set<Item> valid = new HashSet<>();
        if (recipe == null) return valid;
        for (Ingredient ing : recipe.getIngredients()) {
            if (ing.isEmpty()) continue;
            for (ItemStack s : ing.getMatchingStacks()) valid.add(s.getItem());
        }
        return valid;
    }

    private void syncCraftingInventoryFromRecipeGrid() {
        for (int i = 0; i < 9; i++) {
            ItemStack s = recipeInventory.get(i);
            craftingInventory.setStack(i, s.isEmpty() ? ItemStack.EMPTY : s.copy());
        }
    }

    private boolean isRecipeEmpty() {
        for (ItemStack stack : recipeInventory) if (!stack.isEmpty()) return false;
        return true;
    }

    /** Sided inventory: hoppers */
    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? OUTPUT_SLOT : INPUT_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (slot < 0 || slot > 8) return false; // inputs only

        // If we know the recipe, only accept items that are valid for any ingredient
        Set<Item> valid = computeValidItemsForRecipe(cachedRecipe);
        if (!valid.isEmpty() && !valid.contains(stack.getItem())) return false;

        // Per-slot stacking only
        ItemStack current = internalInventory.get(slot);
        if (current.isEmpty()) return true;
        if (!ItemStack.canCombine(current, stack)) return false;

        int max = Math.min(current.getMaxCount(), getMaxCountPerStack());
        return current.getCount() + stack.getCount() <= max;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 9;
    }

    /** Inventory interface (internalInventory only) */
    @Override public int size() { return internalInventory.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : internalInventory) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override public ItemStack getStack(int slot) { return internalInventory.get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(internalInventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(internalInventory, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot < 0 || slot >= internalInventory.size()) return;

        // Enforce ingredient-only for inputs when a recipe is known
        if (slot <= 8 && !stack.isEmpty()) {
            Set<Item> valid = computeValidItemsForRecipe(cachedRecipe);
            if (!valid.isEmpty() && !valid.contains(stack.getItem())) return;
        }

        if (stack.isEmpty()) {
            internalInventory.set(slot, ItemStack.EMPTY);
        } else {
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(copy.getCount(), Math.min(copy.getMaxCount(), getMaxCountPerStack())));
            internalInventory.set(slot, copy);
        }
        markDirty();
    }

    public void recheckRecipe() {
        if (world instanceof ServerWorld serverWorld) {
            syncCraftingInventoryFromRecipeGrid();
            RecipeManager recipeManager = serverWorld.getServer().getRecipeManager();
            cachedRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInventory, serverWorld).orElse(null);
            isRecipeGridDirty = false;
            markDirty();
        }
    }

    @Override
    public void clear() {
        internalInventory.clear();
        markDirty();
    }

    @Override public int getMaxCountPerStack() { return 64; }

    public DefaultedList<ItemStack> getRecipeInventory() { return recipeInventory; }

    public void markRecipeDirty() { this.isRecipeGridDirty = true; }

    /** NBT Save/Load */
    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag.getCompound("InternalInventory"), internalInventory);
        Inventories.readNbt(tag.getCompound("RecipeInventory"), recipeInventory);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.put("InternalInventory", Inventories.writeNbt(new NbtCompound(), internalInventory, true));
        tag.put("RecipeInventory", Inventories.writeNbt(new NbtCompound(), recipeInventory, true));
    }

    /** Drop and clear inventories (no duplication if called repeatedly). */
    public void dropInventory(World world, BlockPos pos) {
        if (world == null || world.isClient()) return;

        for (int i = 0; i < internalInventory.size(); i++) {
            ItemStack stack = internalInventory.get(i);
            if (!stack.isEmpty()) {
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                internalInventory.set(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < recipeInventory.size(); i++) {
            ItemStack stack = recipeInventory.get(i);
            if (!stack.isEmpty()) {
                world.spawnEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                recipeInventory.set(i, ItemStack.EMPTY);
            }
        }
        markDirty();
    }

    /** Dummy handler for CraftingInventory */
    private static class DummyHandler extends ScreenHandler {
        protected DummyHandler() { super(null, 0); }
        @Override public boolean canUse(PlayerEntity player) { return true; }
        @Override public ItemStack quickMove(PlayerEntity player, int index) { return ItemStack.EMPTY; }
    }
}
