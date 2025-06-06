package net.willowins.animewitchery.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.screen.BlockPlacerScreenHandler;
import org.jetbrains.annotations.Nullable;

public class BlockPlacerBlockEntity extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);
    private boolean wasPowered = false;

    public BlockPlacerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLOCK_PLACER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockPlacerBlockEntity be) {
        if (world == null || world.isClient) return;

        boolean powered = world.isReceivingRedstonePower(pos);

        if (powered && !be.wasPowered) {
            be.tryPlaceBlock(world, pos);
        }

        be.wasPowered = powered;
    }

    public void tryPlaceBlock(World world, BlockPos placerPos) {
        Direction facing = world.getBlockState(placerPos).get(Properties.FACING); // or HORIZONTAL_FACING if used
        BlockPos targetPos = placerPos.offset(facing);

        for (int i = 0; i < size(); i++) {
            ItemStack stack = getStack(i);

            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                BlockState stateToPlace = blockItem.getBlock().getDefaultState();

                // Orient block correctly if it supports it
                if (stateToPlace.contains(Properties.FACING)) {
                    stateToPlace = stateToPlace.with(Properties.FACING, facing);
                } else if (stateToPlace.contains(Properties.HORIZONTAL_FACING)) {
                    stateToPlace = stateToPlace.with(Properties.HORIZONTAL_FACING, facing);
                }

                // Replace only air or replaceable blocks
                BlockState existingState = world.getBlockState(targetPos);
                if (!existingState.isAir() && !existingState.getCollisionShape(world, targetPos).isEmpty()) continue;


                world.setBlockState(targetPos, stateToPlace, 3);
                stack.decrement(1);
                markDirty();
                return;
            }
        }
    }


    // Inventory handling
    @Override public int size() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getStack(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeStack(int slot, int amount) { return Inventories.splitStack(inventory, slot, amount); }
    @Override public ItemStack removeStack(int slot) { return Inventories.removeStack(inventory, slot); }
    @Override public void setStack(int slot, ItemStack stack) { inventory.set(slot, stack); markDirty(); }
    @Override public boolean canPlayerUse(PlayerEntity player) {
        return world != null && world.getBlockEntity(pos) == this &&
                player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
    @Override public void clear() { inventory.clear(); }

    // Hopper compatibility
    @Override public int[] getAvailableSlots(Direction side) {
        int[] slots = new int[size()];
        for (int i = 0; i < size(); i++) slots[i] = i;
        return slots;
    }
    @Override public boolean canInsert(int slot, ItemStack stack, Direction dir) { return true; }
    @Override public boolean canExtract(int slot, ItemStack stack, Direction dir) { return true; }

    // NBT
    @Override public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
    }

    @Override public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    // GUI
    @Override public Text getDisplayName() {
        return Text.literal("Block Placer");
    }

    @Override public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlockPlacerScreenHandler(syncId, playerInventory, this);
    }

    @Override public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}
