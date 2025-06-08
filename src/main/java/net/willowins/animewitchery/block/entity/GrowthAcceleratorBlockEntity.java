package net.willowins.animewitchery.block.entity;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.screen.GrowthAcceleratorScreenHandler;

public class GrowthAcceleratorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(54, ItemStack.EMPTY);
    private final SimpleInventory inventory = new SimpleInventory(items.size());
    private final PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(2);

    public GrowthAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_ACCELERATOR_BLOCK_ENTITY, pos, state);
    }

    public Inventory getInventory() {
        return inventory;
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

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        Inventories.readNbt(tag, items);
        for (int i = 0; i < items.size(); i++) {
            inventory.setStack(i, items.get(i));
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        for (int i = 0; i < inventory.size(); i++) {
            items.set(i, inventory.getStack(i));
        }
        Inventories.writeNbt(tag, items);
    }

    public void dropInventory(World world, BlockPos pos) {
        if (world == null || world.isClient) return;
        for (ItemStack stack : inventory.stacks) {
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                world.spawnEntity(itemEntity);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, GrowthAcceleratorBlockEntity entity) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        int fuelTime = entity.getFuelTime();
        int maxFuelTime = entity.getMaxFuelTime();

        // If out of fuel, try to consume from inventory
        if (fuelTime <= 0) {
            for (int i = 0; i < entity.getInventory().size(); i++) {
                ItemStack stack = entity.getInventory().getStack(i);
                if (!stack.isEmpty() && GrowthAcceleratorScreenHandler.isFuel(stack)) {
                    Integer fuelTicks = FuelRegistry.INSTANCE.get(stack.getItem());
                    if (fuelTicks != null && fuelTicks > 0) {
                        entity.setFuelTime(fuelTicks);
                        entity.setMaxFuelTime(fuelTicks);
                        stack.decrement(1);
                        fuelTime = fuelTicks;
                        break;
                    }
                }
            }
        }

        // Only consume fuel if we have some
        if (fuelTime > 0) {
            entity.setFuelTime(fuelTime - 1);

            // Only run growth logic and particles once per second
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
                            }
                        }
                    }
                }

                // Emit particles from top center
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5;

                serverWorld.spawnParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        x, y, z,
                        4,
                        0.3, 0.3, 0.3,
                        0.01
                );
            }
        }
    }
}