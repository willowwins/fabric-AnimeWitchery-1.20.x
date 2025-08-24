package net.willowins.animewitchery.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KamikazeRitualEntity extends Entity implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    private int age = 0;
    private final int LIFETIME = 200; // 10 seconds at 20 ticks/second
    private final int FIRE_ANIMATION_START = 160; // Last 2 seconds (40 ticks)
    
    public KamikazeRitualEntity(World world, BlockPos pos) {
        super(ModEntities.KAMIKAZE_RITUAL, world);
        this.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public KamikazeRitualEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }
    
    @Override
    public void tick() {
        super.tick();
        age++;
        
        // Remove entity after lifetime expires
        if (age >= LIFETIME) {
            this.discard();
            return;
        }
        
        // Keep entity in place
        this.setPosition(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5);
    }
    
    @Override
    public void move(MovementType movementType, Vec3d movement) {
        // Prevent movement
    }
    
    @Override
    protected void initDataTracker() {
        // No data tracking needed
    }
    
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        age = nbt.getInt("Age");
    }
    
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", age);
    }
    
    @Override
    public EntitySpawnS2CPacket createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
    
    // Get current animation state
    public String getCurrentAnimation() {
        if (age >= FIRE_ANIMATION_START) {
            return "fire";
        } else {
            return "idle";
        }
    }
    
    // Get animation progress (0.0 to 1.0)
    public float getAnimationProgress() {
        if (age >= FIRE_ANIMATION_START) {
            // Fire animation: 2 seconds, loop from 0.0 to 1.0
            int fireAge = age - FIRE_ANIMATION_START;
            return (fireAge % 40) / 40.0f; // 40 ticks = 2 seconds
        } else {
            // Idle animation: 8 seconds, loop from 0.0 to 1.0
            return (age % 160) / 160.0f; // 160 ticks = 8 seconds
        }
    }
    
    // Check if we're in the final countdown phase
    public boolean isInFinalCountdown() {
        return age >= FIRE_ANIMATION_START;
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Add idle animation controller for the charging phase
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state -> {
            if (age < FIRE_ANIMATION_START) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("fire"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
