package net.willowins.animewitchery.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class KineticBladeHitboxEntity extends Entity {
    private LivingEntity owner;
    private int age;

    public KineticBladeHitboxEntity(EntityType<? extends KineticBladeHitboxEntity> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    public KineticBladeHitboxEntity(World world, LivingEntity owner) {
        this(ModEntities.KINETIC_BLADE_HITBOX, world);
        this.owner = owner;
        this.setPosition(owner.getX(), owner.getY(), owner.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;

        age++;
        if (owner == null || !owner.isAlive()) {
            discard();
            return;
        }

        // Despawn if player stops using item
        if (owner instanceof PlayerEntity p && !p.isUsingItem()) {
            if (!this.getWorld().isClient && !this.isRemoved()) {
                this.kill();
            }
            return;
        }

        updatePositionToPlayer(owner);
        performDamageLogic();

        if (age > 200) discard();
    }

    public void updatePositionToPlayer(LivingEntity player) {
        Vec3d look = player.getRotationVector().normalize();
        Vec3d velocity = player.getVelocity();

        double predictionFactor = 1.5;
        Vec3d predictedPos = player.getPos()
                .add(velocity.multiply(predictionFactor))
                .add(look.multiply(1.5))
                .add(0, 1.0, 0);

        this.setPosition(predictedPos.x, predictedPos.y, predictedPos.z);

        double baseSize = 2.0;
        double speed = velocity.length();
        double dynamicSize = baseSize + (speed * 1.5);

        this.setBoundingBox(new Box(
                predictedPos.add(-dynamicSize, -dynamicSize, -dynamicSize),
                predictedPos.add(dynamicSize, dynamicSize, dynamicSize)
        ));
    }

    /** Deals kinetic damage based on the owner’s speed and grants resistance. */
    private void performDamageLogic() {
        Box box = this.getBoundingBox();
        List<LivingEntity> targets = getWorld().getEntitiesByClass(LivingEntity.class, box,
                e -> e.isAlive() && e != owner);

        if (targets.isEmpty()) return;

        double speed = owner.getVelocity().length();
        float baseDamage = 7.0f;
        float damage = baseDamage + (float) (speed * 5.0);

        DamageSource src = (owner instanceof PlayerEntity p)
                ? getWorld().getDamageSources().playerAttack(p)
                : getWorld().getDamageSources().mobAttack(owner);

        boolean hitSomething = false;

        for (LivingEntity t : targets) {
            if (t.damage(src, damage)) {
                hitSomething = true;
            }
        }

        // 🛡 Apply Resistance V for 1 second if the aura hit connects
        if (hitSomething && owner != null) {
            owner.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 20, 4, false, false, true));
        }

        if (owner instanceof ServerPlayerEntity sp) {
            sp.sendMessage(Text.literal(String.format("§b⚡ %.1f Kinetic Aura Hit", damage)), true);
        }
    }

    @Override protected void initDataTracker() {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
