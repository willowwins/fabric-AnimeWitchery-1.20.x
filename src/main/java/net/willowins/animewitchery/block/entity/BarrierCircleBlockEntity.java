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
import net.willowins.animewitchery.block.ModBlocks;

public class BarrierCircleBlockEntity extends BlockEntity {
    private CircleStage stage = CircleStage.BASIC; // BASIC -> DEFINED -> COMPLETE
    private CircleType circleType = CircleType.NONE;
    private int ritualActivationStep = 0; // 0=none, 1=east, 2=south, 3=west
    private long step3StartTime = 0; // Track when step 3 started for energy ball timing
    private boolean ritualActive = false; // Track if ritual is currently active
    private long lastIntegrityCheck = 0; // Track last integrity check time
    
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
        nbt.putInt("ritualActivationStep", ritualActivationStep);
        nbt.putLong("step3StartTime", step3StartTime);
        nbt.putBoolean("ritualActive", ritualActive);
        nbt.putLong("lastIntegrityCheck", lastIntegrityCheck);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.stage = CircleStage.valueOf(nbt.getString("stage"));
        this.circleType = CircleType.valueOf(nbt.getString("circleType"));
        this.ritualActivationStep = nbt.getInt("ritualActivationStep");
        this.step3StartTime = nbt.getLong("step3StartTime");
        this.ritualActive = nbt.getBoolean("ritualActive");
        this.lastIntegrityCheck = nbt.getLong("lastIntegrityCheck");
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
    
    public int getRitualActivationStep() {
        return ritualActivationStep;
    }
    
    public void setRitualActivationStep(int step) {
        this.ritualActivationStep = step;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public void advanceRitualStep() {
        this.ritualActivationStep++;
        
        // Record when step 3 starts for energy ball timing
        if (this.ritualActivationStep == 3 && world != null) {
            this.step3StartTime = world.getTime();
        }
        
        // Mark ritual as active when we start the ritual
        if (this.ritualActivationStep > 0) {
            this.ritualActive = true;
        }
        
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public long getStep3StartTime() {
        return step3StartTime;
    }
    
    public boolean isRitualActive() {
        return ritualActive && ritualActivationStep > 3;
    }
    
    public void setRitualActive(boolean active) {
        this.ritualActive = active;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    /**
     * Check if the ritual integrity is maintained
     * Returns true if the ritual is intact, false if it should be deactivated
     */
    public boolean checkRitualIntegrity() {
        if (!ritualActive || world == null || world.isClient) {
            return true; // No integrity check needed if not active or on client
        }
        
        // Check if the barrier circle itself is still intact
        if (!world.getBlockState(pos).isOf(ModBlocks.BARRIER_CIRCLE)) {
            System.out.println("BarrierCircle: Circle destroyed - ritual integrity broken!");
            return false;
        }
        
        // Check if all required obelisks are still present and active
        BlockPos northPos = pos.north(5);
        BlockPos southPos = pos.south(5);
        BlockPos eastPos = pos.east(5);
        BlockPos westPos = pos.west(5);
        
        boolean hasNorth = world.getBlockState(northPos).isOf(ModBlocks.ACTIVE_OBELISK);
        boolean hasSouth = world.getBlockState(southPos).isOf(ModBlocks.ACTIVE_OBELISK);
        boolean hasEast = world.getBlockState(eastPos).isOf(ModBlocks.ACTIVE_OBELISK);
        boolean hasWest = world.getBlockState(westPos).isOf(ModBlocks.ACTIVE_OBELISK);
        
        if (!hasNorth || !hasSouth || !hasEast || !hasWest) {
            System.out.println("BarrierCircle: Obelisk missing - ritual integrity broken! N:" + hasNorth + " S:" + hasSouth + " E:" + hasEast + " W:" + hasWest);
            return false;
        }
        
        return true;
    }
    
    /**
     * Deactivate the ritual and all obelisks
     */
    public void deactivateRitual() {
        if (world == null || world.isClient) return;
        
        System.out.println("BarrierCircle: Deactivating ritual due to integrity failure!");
        
        // Reset ritual state
        this.ritualActive = false;
        this.ritualActivationStep = 0;
        this.step3StartTime = 0;
        
        // Deactivate all obelisks
        BlockPos northPos = pos.north(5);
        BlockPos southPos = pos.south(5);
        BlockPos eastPos = pos.east(5);
        BlockPos westPos = pos.west(5);
        
        deactivateObelisk(world, northPos);
        deactivateObelisk(world, southPos);
        deactivateObelisk(world, eastPos);
        deactivateObelisk(world, westPos);
        
        // Play deactivation sound
        world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_BEACON_DEACTIVATE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
        
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    private void deactivateObelisk(World world, BlockPos obeliskPos) {
        if (world.getBlockState(obeliskPos).isOf(ModBlocks.ACTIVE_OBELISK)) {
            world.setBlockState(obeliskPos, ModBlocks.OBELISK.getDefaultState());
            
            // Spawn deactivation particles
            for (int i = 0; i < 10; i++) {
                double x = obeliskPos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                double y = obeliskPos.getY() + 1.0 + world.random.nextDouble() * 3.0;
                double z = obeliskPos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                
                world.addParticle(net.minecraft.particle.ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
            }
        }
    }
    
    /**
     * Static tick method for integrity checking
     */
    public static void tick(World world, BlockPos pos, BlockState state, BarrierCircleBlockEntity entity) {
        if (world.isClient) return;
        
        // Check integrity every 20 ticks (1 second) if ritual is active
        if (entity.isRitualActive() && world.getTime() - entity.lastIntegrityCheck >= 20) {
            entity.lastIntegrityCheck = world.getTime();
            
            if (!entity.checkRitualIntegrity()) {
                entity.deactivateRitual();
            }
        }
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