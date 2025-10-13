package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProtectedChestBlockEntity extends TrappedChestBlockEntity {
    private final ViewerCountManager stateManager = new ViewerCountManager() {
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            // Let vanilla chest handle sounds
        }

        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            // Let vanilla chest handle sounds
        }

        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            ProtectedChestBlockEntity.this.onViewerCountUpdate(world, pos, state, oldViewerCount, newViewerCount);
        }

        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                return ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory() == ProtectedChestBlockEntity.this;
            }
            return false;
        }
    };
    private UUID ownerUuid;
    private String ownerName;
    private Set<String> authorizedPlayers = new HashSet<>();

    public ProtectedChestBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return BlockEntityType.TRAPPED_CHEST;
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

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public String getOwnerName() {
        return this.ownerName != null ? this.ownerName : "Unknown";
    }

    public boolean isOwner(PlayerEntity player) {
        return this.ownerUuid != null && this.ownerUuid.equals(player.getUuid());
    }

    public boolean isAuthorized(String playerName) {
        return this.authorizedPlayers.contains(playerName);
    }

    public boolean addAuthorizedPlayer(String playerName) {
        boolean added = this.authorizedPlayers.add(playerName);
        if (added) {
            this.markDirty();
        }
        return added;
    }

    public boolean removeAuthorizedPlayer(String playerName) {
        boolean removed = this.authorizedPlayers.remove(playerName);
        if (removed) {
            this.markDirty();
        }
        return removed;
    }

    public Set<String> getAuthorizedPlayers() {
        return new HashSet<>(this.authorizedPlayers);
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

        // Read authorized players
        this.authorizedPlayers.clear();
        if (nbt.contains("AuthorizedPlayers")) {
            NbtList playersList = nbt.getList("AuthorizedPlayers", 8); // 8 = String type
            for (int i = 0; i < playersList.size(); i++) {
                this.authorizedPlayers.add(playersList.getString(i));
            }
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

        // Write authorized players
        NbtList playersList = new NbtList();
        for (String playerName : this.authorizedPlayers) {
            playersList.add(NbtString.of(playerName));
        }
        nbt.put("AuthorizedPlayers", playersList);
    }


    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        // Override this if you need custom behavior when viewer count changes
    }
}

