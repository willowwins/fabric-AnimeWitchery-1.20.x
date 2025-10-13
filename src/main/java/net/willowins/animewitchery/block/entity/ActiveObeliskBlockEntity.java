package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.sound.ModSounds;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ActiveObeliskBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private BlockPos linkedRitualPos;
    private int textureVariant = 0; // Store the texture variant from the original obelisk

    public ActiveObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Active", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("active"))));
    }

    public void startHumSound() {
        // This will be called from the renderer on client side
        if (world != null && world.isClient) {
            world.playSound(null, pos, ModSounds.OBELISK_HUM, SoundCategory.AMBIENT, 0.3f, 1.0f);
        }
    }

    public void stopHumSound() {
        // This will be called when the obelisk is deactivated
        if (world != null && world.isClient) {
            // Stop any ongoing hum sounds by playing a silent sound
            // The hum sound will naturally stop when the block entity is removed
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    public void writeNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.writeNbt(nbt);
        if (linkedRitualPos != null) {
            nbt.putLong("LinkedRitualPos", linkedRitualPos.asLong());
        }
        nbt.putInt("textureVariant", textureVariant);
    }
    
    @Override
    public void readNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("LinkedRitualPos")) {
            linkedRitualPos = BlockPos.fromLong(nbt.getLong("LinkedRitualPos"));
        }
        if (nbt.contains("textureVariant")) {
            textureVariant = nbt.getInt("textureVariant");
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, ActiveObeliskBlockEntity entity) {
        // Play hum sound every 2 seconds (40 ticks)
        if (world.getTime() % 40 == 0 && world.isClient) {
            entity.startHumSound();
        }
        
        // Check ritual integrity every 20 ticks (1 second) on server side
        if (!world.isClient && world.getTime() % 20 == 0) {
            entity.checkRitualIntegrity();
        }
    }
    
    /**
     * Set the linked ritual position
     */
    public void setLinkedRitual(BlockPos ritualPos) {
        this.linkedRitualPos = ritualPos;
        markDirty();
    }
    
    /**
     * Get the linked ritual position
     */
    public BlockPos getLinkedRitualPos() {
        return linkedRitualPos;
    }
    
    /**
     * Get the texture variant
     */
    public int getTextureVariant() {
        return textureVariant;
    }
    
    /**
     * Set the texture variant (called when converting from regular obelisk)
     */
    public void setTextureVariant(int variant) {
        this.textureVariant = variant;
        markDirty();
    }
    
    /**
     * Check if this obelisk is still part of an active ritual
     * If not, deactivate itself
     */
    private void checkRitualIntegrity() {
        if (world == null || world.isClient) return;
        
        // Check if we have a linked ritual position
        if (linkedRitualPos == null) {
            deactivateObelisk();
            return;
        }
        
        // Check if the barrier circle still exists and is active
        BlockEntity blockEntity = world.getBlockEntity(linkedRitualPos);
        if (blockEntity instanceof BarrierCircleBlockEntity barrierCircle) {
            if (barrierCircle.checkRitualIntegrity()) {
                return; // Ritual is still active
            }
        }
        
        // Ritual is no longer valid, deactivate
        //deactivateObelisk();
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
    
    /**
     * Deactivate this obelisk
     */
    private void deactivateObelisk() {
        if (world == null || world.isClient) return;
        
        // Convert back to regular obelisk
        world.setBlockState(pos, ModBlocks.OBELISK.getDefaultState());
        
        // Set the texture variant on the new obelisk block entity
        BlockEntity newObelisk = world.getBlockEntity(pos);
        if (newObelisk instanceof ObeliskBlockEntity obeliskEntity) {
            obeliskEntity.setTextureVariant(textureVariant);
        }
        
        // Spawn deactivation particles
        for (int i = 0; i < 15; i++) {
            double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            double y = pos.getY() + 1.0 + world.random.nextDouble() * 3.0;
            double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
            
            world.addParticle(net.minecraft.particle.ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
        }
        
        // Play deactivation sound
        world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
} 