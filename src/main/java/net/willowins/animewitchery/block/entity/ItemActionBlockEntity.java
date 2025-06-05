package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.screen.ItemActionScreenHandler;
import net.willowins.animewitchery.util.ItemStackHelper;


public class ItemActionBlockEntity extends BlockEntity implements Inventory, NamedScreenHandlerFactory {

    private static final int INVENTORY_SIZE = 9;
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public ItemActionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_ACTION_BLOCK_ENTITY, pos, state);
    }

    // Inventory methods

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = ItemStackHelper.splitStack(items, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = ItemStackHelper.removeStack(items, slot);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (world == null || world.getBlockEntity(pos) != this) return false;
        return player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clear() {
        items.clear();
        markDirty();
    }

    // Screen handler creation

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ItemActionScreenHandler(ModScreenHandlers.ITEM_ACTION_SCREEN_HANDLER, syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Interactor");
    }

    // NBT saving/loading

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        items = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        Inventories.readNbt(tag, items);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        Inventories.writeNbt(tag, items);
    }

    // Drops inventory contents in the world

    public void dropInventory(World world, BlockPos pos) {
        if (world == null || world.isClient()) return;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                world.spawnEntity(itemEntity);
            }
        }
    }

    // Ticking method called each tick on server side

    public static void tick(World world, BlockPos pos, BlockState state, ItemActionBlockEntity blockEntity) {
        if (world.isClient()) return;

        // Run once every second (20 ticks)
        if (world.getTime() % 20 != 0) return;

        // Check if inventory contains Blaze Sack
        boolean hasBlazeSack = false;
        int blazeSackSlot = -1;

        for (int i = 0; i < blockEntity.size(); i++) {
            ItemStack stack = blockEntity.getStack(i);
            if (!stack.isEmpty() && stack.isOf(ModItems.BLAZE_SACK)) {
                hasBlazeSack = true;
                blazeSackSlot = i;
                break;
            }
        }

        if (!hasBlazeSack) return;

        // Scan in 1 block radius around the block entity
        int radius = 1;

        boolean brokeSomething = false;

        outerLoop:
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    if (world.getBlockState(checkPos).isOf(Blocks.AMETHYST_CLUSTER)) {
                        // Break the amethyst cluster without dropping default drops
                        world.breakBlock(checkPos, false);

                        // Spawn the Alchemical Catalyst drop
                        ItemStack dropStack = new ItemStack(ModItems.ALCHEMICAL_CATALYST);
                        ItemEntity entity = new ItemEntity(world, checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5, dropStack);
                        world.spawnEntity(entity);

                        // Consume one Blaze Sack from inventory
                        ItemStack blazeSackStack = blockEntity.getStack(blazeSackSlot);
                        blazeSackStack.decrement(1);
                        if (blazeSackStack.isEmpty()) {
                            blockEntity.setStack(blazeSackSlot, ItemStack.EMPTY);
                        } else {
                            blockEntity.setStack(blazeSackSlot, blazeSackStack);
                        }

                        brokeSomething = true;
                        break outerLoop; // Only break one cluster per tick
                    }
                }
            }
        }

        if (brokeSomething) {
            blockEntity.markDirty();
        }
    }
}
