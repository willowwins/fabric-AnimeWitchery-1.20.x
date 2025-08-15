package net.willowins.animewitchery.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class VoidWispEntity extends PhantomEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public VoidWispEntity(EntityType<? extends PhantomEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        // Call super to get PhantomEntity's flying AI, then modify it
        super.initGoals();
        
        // Clear the default target selector and add our own
        this.targetSelector.clear((goal) -> true);
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        
        // Keep PhantomEntity's flying goals but ensure it targets players
        // The StartAttackGoal, SwoopMovementGoal, and CircleMovementGoal will now work with our targeting
    }

    public static DefaultAttributeContainer.Builder createVoidWispAttributes() {
        return PhantomEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // Override to give it a more appropriate hitbox for a void wisp
        // Smaller and more compact than a phantom
        return EntityDimensions.fixed(0.8f, 1.2f); // Width: 0.8, Height: 1.2
    }

    @Override
    public boolean shouldRender(double distance) {
        // Override to make the void wisp render from much further away
        // Default is usually around 64 blocks, let's make it 128+ blocks
        return distance < 128.0 * 128.0; // 128 blocks render distance
    }

    @Override
    public boolean canBreatheInWater() {
        return true; // Void entities can breathe underwater
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        // Make it immune to drowning and other environmental damage
        World world = this.getWorld();
        if (world != null) {
            DamageSources damageSources = world.getDamageSources();
            
            // Check if it's one of the damage types we want to be immune to
            if (damageSource == damageSources.drown() ||
                damageSource == damageSources.fall() ||
                damageSource == damageSources.freeze() ||
                damageSource == damageSources.inFire() ||
                damageSource == damageSources.onFire() ||
                damageSource == damageSources.hotFloor()) {
                return true;
            }
        }
        
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }



    @Override
    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        // Flying entities can spawn in the air, just need low light
        return world.getBaseLightLevel(getBlockPos(), 0) <= 7;
    }

    @Override
    public void tick() {
        super.tick();
        
        // Prevent the entity from going underwater
        if (this.isSubmergedInWater()) {
            // Push it up out of water
            this.setVelocity(this.getVelocity().add(0.0D, 0.1D, 0.0D));
        }
        
        // Prevent sunlight damage
        World world = this.getWorld();
        if (world != null && world.isDay() && world.isSkyVisible(this.getBlockPos())) {
            // If it's daytime and the sky is visible, add some protection
            this.setFireTicks(0); // Remove any fire ticks
        }
        
        // PhantomEntity's built-in flying AI will handle movement properly now
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Add idle animation controller
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop("Idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
