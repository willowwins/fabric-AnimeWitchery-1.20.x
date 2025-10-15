package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.willowins.animewitchery.screen.GrowthAcceleratorScreenHandler;

public class GrowthAcceleratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, SidedInventory {

    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(54, ItemStack.EMPTY);
    private final PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2); // 0 = fuelTime, 1 = maxFuelTime

    public GrowthAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_ACCELERATOR_BLOCK_ENTITY, pos, state);
    }

    public PropertyDelegate getPropertyDelegate() {
        return propertyDelegate;
    }

    public int getFuelTime() {
        return propertyDelegate.get(0);
    }

    public void setFuelTime(int time) {
        propertyDelegate.set(0, time);
    }

    public int getMaxFuelTime() {
        return propertyDelegate.get(1);
    }

    public void setMaxFuelTime(int maxTime) {
        propertyDelegate.set(1, maxTime);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Growth Accelerator");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new GrowthAcceleratorScreenHandler(syncId, playerInventory, this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, GrowthAcceleratorBlockEntity entity) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        int fuelTime = entity.getFuelTime();

        if (fuelTime <= 0) {
            for (int i = 0; i < entity.stacks.size(); i++) {
                ItemStack stack = entity.stacks.get(i);
                if (!stack.isEmpty() && GrowthAcceleratorScreenHandler.isFuel(stack)) {
                    Integer fuelTicks = FuelRegistry.INSTANCE.get(stack.getItem());
                    if (fuelTicks != null && fuelTicks > 0) {
                        entity.setFuelTime(fuelTicks);
                        entity.setMaxFuelTime(fuelTicks);
                        stack.decrement(1);
                        break;
                    }
                }
            }
        }

        if (entity.getFuelTime() > 0) {
            entity.setFuelTime(entity.getFuelTime() - 1);

            if (world.getTime() % 20 == 0) {
                int radius = 3;

                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            BlockPos checkPos = pos.add(dx, dy, dz);
                            BlockState checkState = world.getBlockState(checkPos);
                            Block block = checkState.getBlock();

                            if (block instanceof CropBlock crop && !crop.isMature(checkState)) {
                                crop.randomTick(checkState, serverWorld, checkPos, world.random);
                            } else if (block instanceof SaplingBlock sapling) {
                                sapling.randomTick(checkState, serverWorld, checkPos, world.random);
                            } else if (block instanceof BuddingAmethystBlock budding) {
                                budding.randomTick(checkState, serverWorld, checkPos, world.random);
                            } else if (block instanceof AmethystClusterBlock cluster) {
                                cluster.randomTick(checkState, serverWorld, checkPos, world.random);
                            } else if (block instanceof PointedDripstoneBlock dripstone) {
                                dripstone.randomTick(checkState, serverWorld, checkPos, world.random);
                            }
                        }
                    }
                }

                serverWorld.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        4,
                        0.3, 0.3, 0.3,
                        0.01
                );
            }
        }
    }

    // --------------------------
    // Inventory (SidedInventory)
    // --------------------------

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(stacks, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(stacks, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clear() {
        stacks.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[stacks.size()];
        for (int i = 0; i < stacks.size(); i++) result[i] = i;
        return result;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return GrowthAcceleratorScreenHandler.isFuel(stack); // Only fuel is insertable
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    // --------------------------
    // NBT (save/load inventory)
    // --------------------------

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, stacks);
        nbt.putInt("FuelTime", getFuelTime());
        nbt.putInt("MaxFuelTime", getMaxFuelTime());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, stacks);
        setFuelTime(nbt.getInt("FuelTime"));
        setMaxFuelTime(nbt.getInt("MaxFuelTime"));
    }
}
