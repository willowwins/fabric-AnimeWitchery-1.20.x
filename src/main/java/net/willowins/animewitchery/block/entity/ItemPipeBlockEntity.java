package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Set;

public class ItemPipeBlockEntity extends BlockEntity implements GeoAnimatable {

    private final Set<Direction> inputs = EnumSet.noneOf(Direction.class);
    private final Set<Direction> outputs = EnumSet.noneOf(Direction.class);
    private DefaultedList<ItemStack> internalInventory = DefaultedList.ofSize(64, ItemStack.EMPTY);

    private int tickCounter = 0;
    private int retryCooldown = 0;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_PIPE_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            inputs.add(dir);
            outputs.add(dir);
        }
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T be) {
        if (be instanceof ItemPipeBlockEntity pipe) {
            pipe.tick();
        }
    }

    public void tick() {
        if (world == null || world.isClient) return;

        tickCounter++;
        if (tickCounter < 10) return;
        tickCounter = 0;

        boolean changed = false;
        boolean movedItem = false;

        // Attempt to move items to outputs
        for (Direction outputDir : outputs) {
            BlockPos outputPos = pos.offset(outputDir);
            Inventory outputInv = getInventoryAt(outputPos, outputDir.getOpposite());

            if (outputInv == null) continue;

            for (int i = 0; i < internalInventory.size(); i++) {
                ItemStack stack = internalInventory.get(i);
                if (!stack.isEmpty()) {
                    ItemStack leftover = insertItemToInventory(outputInv, stack);
                    if (leftover.getCount() < stack.getCount()) {
                        internalInventory.set(i, leftover);
                        changed = true;
                        movedItem = true;
                        System.out.println("[ItemPipe] Moved " + (stack.getCount() - leftover.getCount()) + "x " + stack.getItem() + " to " + outputPos);
                        spawnTransferParticles(outputPos);
                        break;
                    }
                }
            }
        }

        if (!movedItem) {
            retryCooldown++;
            if (retryCooldown >= 2) {
                retryCooldown = 0;
                for (Direction outputDir : outputs) {
                    BlockPos outputPos = pos.offset(outputDir);
                    Inventory outputInv = getInventoryAt(outputPos, outputDir.getOpposite());
                    if (outputInv == null) continue;

                    for (int i = 0; i < internalInventory.size(); i++) {
                        ItemStack stack = internalInventory.get(i);
                        if (!stack.isEmpty()) {
                            ItemStack leftover = insertItemToInventory(outputInv, stack);
                            if (leftover.getCount() < stack.getCount()) {
                                internalInventory.set(i, leftover);
                                changed = true;
                                System.out.println("[ItemPipe] Retried and moved " + (stack.getCount() - leftover.getCount()) + "x " + stack.getItem());
                                spawnTransferParticles(outputPos);
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            retryCooldown = 0;
        }

        // Attempt to pull from inputs
        for (Direction inputDir : inputs) {
            BlockPos inputPos = pos.offset(inputDir);
            Inventory inputInv = getInventoryAt(inputPos, inputDir.getOpposite());

            if (inputInv == null) continue;
            if (isBufferFull()) break;

            boolean outputSpaceAvailable = outputs.stream()
                    .anyMatch(dir -> aboveCanAcceptAnyItem(getInventoryAt(pos.offset(dir), dir.getOpposite())));
            if (!outputSpaceAvailable) {
                System.out.println("[ItemPipe] Skipping pull from " + inputDir + " due to no output space.");
                break;
            }

            boolean pulled = false;
            for (int slot = 0; slot < inputInv.size(); slot++) {
                ItemStack stack = inputInv.getStack(slot);
                if (!stack.isEmpty()) {
                    ItemStack oneItem = stack.copy();
                    oneItem.setCount(1);
                    if (simulateInsertInternalInventory(oneItem).isEmpty()) {
                        stack.decrement(1);
                        inputInv.setStack(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
                        ItemStack leftover = insertIntoInternalInventory(oneItem);
                        if (!leftover.isEmpty()) {
                            System.err.println("[ItemPipe] Lost item while pulling: " + leftover);
                        } else {
                            System.out.println("[ItemPipe] Pulled 1x " + oneItem.getItem() + " from " + inputPos);
                        }
                        changed = true;
                        pulled = true;
                        break;
                    }
                }
            }
            if (pulled) break;
        }

        if (changed) {
            markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private boolean isBufferFull() {
        for (ItemStack stack : internalInventory) {
            if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) return false;
        }
        return true;
    }

    private boolean aboveCanAcceptAnyItem(@Nullable Inventory inv) {
        if (inv == null) return false;

        for (ItemStack stack : internalInventory) {
            if (!stack.isEmpty()) {
                ItemStack testStack = stack.copy();
                ItemStack result = insertItemToInventorySimulated(inv, testStack);
                if (result.getCount() < testStack.getCount()) return true;
            }
        }
        return false;
    }

    private ItemStack insertItemToInventorySimulated(Inventory inventory, ItemStack stack) {
        ItemStack remainder = stack.copy();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slot = inventory.getStack(i);
            if (slot.isEmpty()) {
                return ItemStack.EMPTY;
            } else if (ItemStack.canCombine(slot, remainder)) {
                int space = slot.getMaxCount() - slot.getCount();
                int toMove = Math.min(space, remainder.getCount());
                remainder.decrement(toMove);
                if (remainder.isEmpty()) return ItemStack.EMPTY;
            }
        }

        return remainder;
    }

    private Inventory getInventoryAt(BlockPos pos, Direction fromDir) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be == null) return null;

        if (be instanceof SidedInventory sidedInv) {
            return new SidedInventoryWrapper(sidedInv, fromDir);
        } else if (be instanceof Inventory inv) {
            return inv;
        }
        return null;
    }

    private ItemStack insertItemToInventory(Inventory inventory, ItemStack stack) {
        ItemStack remainder = stack.copy();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (canInsert(inventory, remainder, i) && !slotStack.isEmpty() && ItemStack.canCombine(slotStack, remainder)) {
                int space = slotStack.getMaxCount() - slotStack.getCount();
                int toInsert = Math.min(space, remainder.getCount());
                if (toInsert > 0) {
                    slotStack.increment(toInsert);
                    remainder.decrement(toInsert);
                    inventory.setStack(i, slotStack);
                    inventory.markDirty();
                    if (remainder.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (slotStack.isEmpty() && canInsert(inventory, remainder, i)) {
                inventory.setStack(i, remainder);
                inventory.markDirty();
                return ItemStack.EMPTY;
            }
        }

        return remainder;
    }

    private boolean canInsert(Inventory inventory, ItemStack stack, int slot) {
        if (inventory instanceof SidedInventory sidedInventory) {
            return sidedInventory.canInsert(slot, stack, Direction.UP);
        }
        return true;
    }

    private ItemStack insertIntoInternalInventory(ItemStack stack) {
        ItemStack remainder = stack.copy();
        for (int i = 0; i < internalInventory.size(); i++) {
            ItemStack slot = internalInventory.get(i);
            if (ItemStack.canCombine(slot, remainder)) {
                int space = slot.getMaxCount() - slot.getCount();
                int toMove = Math.min(space, remainder.getCount());
                slot.increment(toMove);
                remainder.decrement(toMove);
                internalInventory.set(i, slot);
                if (remainder.isEmpty()) return ItemStack.EMPTY;
            }
        }
        for (int i = 0; i < internalInventory.size(); i++) {
            if (internalInventory.get(i).isEmpty()) {
                internalInventory.set(i, remainder);
                return ItemStack.EMPTY;
            }
        }
        return remainder;
    }

    private ItemStack simulateInsertInternalInventory(ItemStack stack) {
        ItemStack test = stack.copy();
        for (ItemStack slot : internalInventory) {
            if (ItemStack.canCombine(slot, test)) {
                int space = slot.getMaxCount() - slot.getCount();
                int move = Math.min(space, test.getCount());
                test.decrement(move);
                if (test.isEmpty()) return ItemStack.EMPTY;
            }
        }
        if (internalInventory.stream().anyMatch(ItemStack::isEmpty)) return ItemStack.EMPTY;
        return test;
    }

    private void spawnTransferParticles(BlockPos pos) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    5, 0.25, 0.25, 0.25, 0.01);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, internalInventory);
        int inputBits = 0, outputBits = 0;
        for (Direction dir : inputs) inputBits |= 1 << dir.ordinal();
        for (Direction dir : outputs) outputBits |= 1 << dir.ordinal();
        nbt.putInt("Inputs", inputBits);
        nbt.putInt("Outputs", outputBits);
        nbt.putInt("TickCounter", tickCounter);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        internalInventory = DefaultedList.ofSize(64, ItemStack.EMPTY);
        Inventories.readNbt(nbt, internalInventory);
        tickCounter = nbt.getInt("TickCounter");

        inputs.clear();
        outputs.clear();
        int inputBits = nbt.getInt("Inputs");
        int outputBits = nbt.getInt("Outputs");
        for (Direction dir : Direction.values()) {
            if ((inputBits & (1 << dir.ordinal())) != 0) inputs.add(dir);
            if ((outputBits & (1 << dir.ordinal())) != 0) outputs.add(dir);
        }
    }

    @Override
    public double getTick(Object animatable) {
        return world != null ? world.getTime() : 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static class SidedInventoryWrapper implements Inventory {
        private final SidedInventory inv;
        private final Direction side;
        private final int[] sideSlots;

        public SidedInventoryWrapper(SidedInventory inv, Direction side) {
            this.inv = inv;
            this.side = side;
            this.sideSlots = inv.getAvailableSlots(side);
        }

        @Override public int size() { return sideSlots.length; }

        @Override public boolean isEmpty() {
            for (int slot : sideSlots) {
                if (!inv.getStack(slot).isEmpty()) return false;
            }
            return true;
        }

        @Override public ItemStack getStack(int i) {
            return i >= 0 && i < sideSlots.length ? inv.getStack(sideSlots[i]) : ItemStack.EMPTY;
        }

        @Override public ItemStack removeStack(int i, int amount) {
            return i >= 0 && i < sideSlots.length ? inv.removeStack(sideSlots[i], amount) : ItemStack.EMPTY;
        }

        @Override public ItemStack removeStack(int i) {
            return i >= 0 && i < sideSlots.length ? inv.removeStack(sideSlots[i]) : ItemStack.EMPTY;
        }

        @Override public void setStack(int i, ItemStack stack) {
            if (i >= 0 && i < sideSlots.length) inv.setStack(sideSlots[i], stack);
        }

        @Override public void markDirty() { inv.markDirty(); }

        @Override public boolean canPlayerUse(PlayerEntity player) { return inv.canPlayerUse(player); }

        @Override public void clear() {
            for (int slot : sideSlots) {
                inv.setStack(slot, ItemStack.EMPTY);
            }
        }
    }
}
