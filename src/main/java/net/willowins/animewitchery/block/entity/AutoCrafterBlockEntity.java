package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class AutoCrafterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory {

    private final DefaultedList<ItemStack> recipeInventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> internalInventory = DefaultedList.ofSize(10, ItemStack.EMPTY);

    private static final int[] INPUT_SLOTS = IntStream.range(0, 9).toArray();
    private static final int[] OUTPUT_SLOT = new int[]{9};

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

    public static void tick(World world, BlockPos pos, BlockState state, AutoCrafterBlockEntity be) {
        if (world.isClient || be.isRecipeEmpty()) return;

        CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, 0) {
            @Override public ItemStack quickMove(PlayerEntity player, int slot) { return ItemStack.EMPTY; }
            @Override public boolean canUse(PlayerEntity player) { return true; }
        }, 3, 3);

        for (int i = 0; i < 9; i++) {
            craftingInventory.setStack(i, be.recipeInventory.get(i));
        }

        RecipeManager recipeManager = ((ServerWorld) world).getServer().getRecipeManager();
        Optional<CraftingRecipe> recipeOpt = recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (recipeOpt.isEmpty()) return;

        CraftingRecipe recipe = recipeOpt.get();

        // 💥 Clean up internal inventory
        be.dropNonMatchingItems(world, pos, recipe);

        ItemStack result = recipe.craft(craftingInventory, world.getRegistryManager());
        if (result.isEmpty()) return;

        ItemStack outputStack = be.internalInventory.get(9);
        boolean canInsertOutput = outputStack.isEmpty() ||
                (ItemStack.canCombine(outputStack, result) && outputStack.getCount() + result.getCount() <= outputStack.getMaxCount());

        if (!canInsertOutput || !be.hasIngredients(recipe)) return;

        be.consumeIngredients(recipe);

        if (outputStack.isEmpty()) {
            be.internalInventory.set(9, result.copy());
        } else {
            outputStack.increment(result.getCount());
        }

        be.markDirty();
    }
    private void dropNonMatchingItems(World world, BlockPos pos, CraftingRecipe recipe) {
        var validItems = recipe.getIngredients().stream()
                .flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
                .map(ItemStack::getItem)
                .toList();

        for (int i = 0; i < 9; i++) {
            ItemStack stack = internalInventory.get(i);
            if (!stack.isEmpty() && !validItems.contains(stack.getItem())) {
                // Drop into the world
                ItemScatterer.spawn(world, pos.getX(), pos.getY()+2, pos.getZ(), stack);
                internalInventory.set(i, ItemStack.EMPTY);
            }
        }
    }

    private boolean isRecipeEmpty() {
        for (ItemStack stack : recipeInventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    private boolean hasIngredients(CraftingRecipe recipe) {
        DefaultedList<ItemStack> tempInventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            tempInventory.set(i, internalInventory.get(i).copy());
        }

        var ingredients = recipe.getIngredients();

        for (var ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            boolean matched = false;

            for (int j = 0; j < tempInventory.size(); j++) {
                ItemStack stack = tempInventory.get(j);
                if (ingredient.test(stack) && stack.getCount() > 0) {
                    stack.decrement(1);
                    matched = true;
                    break;
                }
            }

            if (!matched) return false;
        }

        return true;
    }

    private void consumeIngredients(CraftingRecipe recipe) {
        var ingredients = recipe.getIngredients();

        for (var ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;

            for (int i = 0; i < 9; i++) {
                ItemStack stack = internalInventory.get(i);
                if (ingredient.test(stack) && stack.getCount() > 0) {
                    stack.decrement(1);
                    break;
                }
            }
        }
    }

    // -- Hopper Interaction --

    @Override
    public int[] getAvailableSlots(Direction side) {
        return side == Direction.DOWN ? OUTPUT_SLOT : INPUT_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        if (slot < 0 || slot >= 9) return false; // only input slots 0-8

        ItemStack current = internalInventory.get(slot);
        // Allow insertion only if slot is empty or not full and items are stackable and matching
        return current.isEmpty()
                || (ItemStack.canCombine(current, stack) && current.getCount() < current.getMaxCount());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 9;
    }

    // -- Inventory (internalInventory only) --

    @Override
    public int size() {
        return internalInventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : internalInventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return internalInventory.get(slot);
    }

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
        if (slot >= 0 && slot < internalInventory.size()) {
            if (!stack.isEmpty()) {
                // Clamp the count to max stack size for this item
                int maxCount = Math.min(stack.getCount(), stack.getMaxCount());
                ItemStack clampedStack = stack.copy();
                clampedStack.setCount(maxCount);
                internalInventory.set(slot, clampedStack);
            } else {
                internalInventory.set(slot, ItemStack.EMPTY);
            }
            markDirty();
        }
    }

    @Override
    public void clear() {
        internalInventory.clear();
        markDirty();
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    public DefaultedList<ItemStack> getRecipeInventory() {
        return recipeInventory;
    }

    // -- NBT Save/Load --

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
}
