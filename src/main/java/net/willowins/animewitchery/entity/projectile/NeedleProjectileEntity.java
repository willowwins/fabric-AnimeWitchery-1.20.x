package net.willowins.animewitchery.entity.projectile;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.entity.ModEntities;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimationState;


public class NeedleProjectileEntity extends PersistentProjectileEntity implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NeedleProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }


    public NeedleProjectileEntity(World world, PlayerEntity owner) {
        super(ModEntities.NEEDLE_PROJECTILE, owner, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }
    @Override
    public void tick() {
        super.tick();

        Vec3d velocity = this.getVelocity();
        if (!velocity.equals(Vec3d.ZERO)) {
            // Fix yaw to face direction horizontally (Minecraft yaw zero is south, +Z)
            double yaw = MathHelper.atan2(velocity.z, velocity.x) * (180F / Math.PI)-180;

            // Fix pitch to face vertical direction correctly
            double pitch =  MathHelper.atan2(velocity.y, velocity.horizontalLength()) * (180F / Math.PI);

            this.setYaw((float) yaw);
            this.setPitch((float) pitch);
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
    }




    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        // You can add particle, sound, damage, or effects here
        target.damage(target.getDamageSources().magic(),15.0f);
        target.addStatusEffect(new StatusEffectInstance(ModEffect.MARKED, 200, 0));
        this.discard(); // Remove projectile on hit
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }


    // This is the method your error asks for:
    public <E extends GeoAnimatable> PlayState getTick(E animatable, long instanceId, AnimationState<E> animationState) {
        return PlayState.CONTINUE;
    }
}

