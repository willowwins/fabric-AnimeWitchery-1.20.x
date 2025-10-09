package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

public class GrandShulkerBoxBlockEntity extends BlockEntity
        implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world == null || this.world.getBlockEntity(this.pos) != this) return false;
        return player.squaredDistanceTo(
                (double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D,
                (double) this.pos.getZ() + 0.5D
        ) <= 64.0D;
    }

    public GrandShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.GRAND_SHULKER_BOX_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Grand Shulker Box");
    }


    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new net.willowins.animewitchery.screen.GrandShulkerBoxScreenHandler(
                net.willowins.animewitchery.ModScreenHandlers.GRAND_SHULKER_BOX_SCREEN_HANDLER, 
                syncId, 
                playerInventory, 
                this
        );
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
    }
    
    /**
     * Check if the inventory is empty (all slots contain empty stacks)
     */
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get the number of non-empty slots
     */
    public int getOccupiedSlots() {
        int count = 0;
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getMaxCountPerStack() {
        return 128;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
