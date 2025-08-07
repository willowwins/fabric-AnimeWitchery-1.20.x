package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BarrierCircleBlockEntity extends BlockEntity {
    private CircleStage stage = CircleStage.BASIC; // BASIC -> DEFINED -> COMPLETE
    private CircleType circleType = CircleType.NONE;
    
    public enum CircleStage {
        BASIC,      // Just created - shows basic outline
        DEFINED,    // Type set with Barrier Catalyst
        COMPLETE    // Fully drawn circle
    }
    
    public enum CircleType {
        NONE,
        BARRIER     // For obelisk placement
    }

    public BarrierCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("stage", stage.name());
        nbt.putString("circleType", circleType.name());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.stage = CircleStage.valueOf(nbt.getString("stage"));
        this.circleType = CircleType.valueOf(nbt.getString("circleType"));
    }

    public CircleStage getStage() {
        return stage;
    }

    public void setStage(CircleStage stage) {
        System.out.println("BarrierCircleBlockEntity: Setting stage from " + this.stage + " to " + stage);
        this.stage = stage;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            // Force a block update to ensure the renderer gets notified
            world.updateNeighbors(pos, getCachedState().getBlock());
        }
    }

    public CircleType getCircleType() {
        return circleType;
    }

    public void setCircleType(CircleType circleType) {
        System.out.println("BarrierCircleBlockEntity: Setting circle type from " + this.circleType + " to " + circleType);
        this.circleType = circleType;
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            // Force a block update to ensure the renderer gets notified
            world.updateNeighbors(pos, getCachedState().getBlock());
        }
    }

    public boolean isComplete() {
        return stage == CircleStage.COMPLETE;
    }

    public boolean isDefined() {
        return stage == CircleStage.DEFINED || stage == CircleStage.COMPLETE;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt(); // Send full NBT to client when chunk is loaded
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        NbtCompound nbt = new NbtCompound();
        writeNbt(nbt);
        return BlockEntityUpdateS2CPacket.create(this);
    }

} 