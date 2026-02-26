package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity; // Changed import
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class ProtectedChestBlockEntity extends ChestBlockEntity { // Changed extension
    private final ViewerCountManager stateManager = new ViewerCountManager() {
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            // Let vanilla chest handle sounds
        }

        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            // Let vanilla chest handle sounds
        }

        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount,
                int newViewerCount) {
            ProtectedChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
        }

        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                return ((GenericContainerScreenHandler) player.currentScreenHandler)
                        .getInventory() == ProtectedChestBlockEntity.this;
            }
            return false;
        }
    };
    private UUID ownerUuid;
    private String ownerName;
    private String lockName = null;

    public ProtectedChestBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return net.willowins.animewitchery.block.ModBlocks.PROTECTED_CHEST_ENTITY;
    }

    @Override
    protected Text getContainerName() {
        return Text.literal("Protected Chest");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public int size() {
        return 27;
    }

    public void setOwner(UUID uuid, String name) {
        this.ownerUuid = uuid;
        this.ownerName = name;
        this.markDirty();
    }

    public void setLockName(String name) {
        this.lockName = name;
        this.markDirty();
    }

    public String getLockName() {
        return this.lockName;
    }

    public boolean isLocked() {
        return this.lockName != null && !this.lockName.isEmpty();
    }

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public String getOwnerName() {
        return this.ownerName != null ? this.ownerName : "Unknown";
    }

    public boolean isOwner(PlayerEntity player) {
        return this.ownerUuid != null && this.ownerUuid.equals(player.getUuid());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        // Read owner data
        if (nbt.containsUuid("OwnerUuid")) {
            this.ownerUuid = nbt.getUuid("OwnerUuid");
        }
        if (nbt.contains("OwnerName")) {
            this.ownerName = nbt.getString("OwnerName");
        }

        // Read Lock Name
        if (nbt.contains("LockName")) {
            this.lockName = nbt.getString("LockName");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        // Write owner data
        if (this.ownerUuid != null) {
            nbt.putUuid("OwnerUuid", this.ownerUuid);
        }
        if (this.ownerName != null) {
            nbt.putString("OwnerName", this.ownerName);
        }

        // Write Lock Name
        if (this.lockName != null) {
            nbt.putString("LockName", this.lockName);
        }
    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount,
            int newViewerCount) {
        // Override this if you need custom behavior when viewer count changes
    }

    @Nullable
    @Override
    public net.minecraft.network.packet.Packet<net.minecraft.network.listener.ClientPlayPacketListener> toUpdatePacket() {
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
