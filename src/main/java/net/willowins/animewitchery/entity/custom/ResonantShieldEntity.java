package net.willowins.animewitchery.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ModEntities;

public class ResonantShieldEntity extends Entity {
    private static final TrackedData<Float> DAMAGE_ABSORBED = DataTracker.registerData(ResonantShieldEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(ResonantShieldEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    private int ageTick = 0;

    public ResonantShieldEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public ResonantShieldEntity(World world, PlayerEntity owner) {
        this(ModEntities.RESONANT_SHIELD, world);
        this.setOwner(owner);
        this.setPosition(owner.getX(), owner.getEyeY(), owner.getZ());
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE_ABSORBED, 0.0f);
        this.dataTracker.startTracking(OWNER_ID, 0);
    }

    public void setOwner(PlayerEntity owner) {
        this.dataTracker.set(OWNER_ID, owner.getId());
    }

    public PlayerEntity getOwner() {
        int id = this.dataTracker.get(OWNER_ID);
        Entity entity = this.getWorld().getEntityById(id);
        if (entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            // Client-side rendering/sync logic handled by renderer/data tracker
        } else {
            PlayerEntity owner = getOwner();
            if (owner == null || !owner.isUsingItem()) {
                this.discard();
                return;
            }

            // Position in front of the owner
            Vec3d lookVec = owner.getRotationVector();
            double distance = 1.5; // Distance in front
            Vec3d pos = owner.getEyePos().add(lookVec.multiply(distance));

            // Center the entity vertically on the target position
            this.setPosition(pos.x, pos.y - this.getHeight() / 2.0, pos.z);

            // Rotation matches owner (yaw)
            this.setYaw(owner.getYaw());
            this.setPitch(owner.getPitch());

            ageTick++;
        }
    }

    @Override
    public boolean canHit() {
        return true; // Allows raycasts to hit this entity
    }

    @Override
    public float getTargetingMargin() {
        return 0.5f; // Extra margin to catch raycasts slightly missing the hitbox
    }

    @Override
    public boolean isPushable() {
        return true; // Allows collision interaction
    }

    @Override
    public boolean collidesWith(Entity other) {
        // Collide with everything except owner
        return other != getOwner();
    }

    @Override
    public boolean isInvulnerable() {
        return false; // Must be false so projectiles/attacks call damage() for us to absorb them
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient)
            return false;

        // Absorb damage
        float currentAbsorbed = this.dataTracker.get(DAMAGE_ABSORBED);
        this.dataTracker.set(DAMAGE_ABSORBED, currentAbsorbed + amount);

        // Recalculate dimensions based on damage
        this.calculateDimensions();

        return true; // Return true to indicate interaction happened
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float absorbed = this.dataTracker.get(DAMAGE_ABSORBED);
        // Base size 1x1, grow by 0.1 for every 5 damage?
        float scale = 1.0f + (absorbed / 20.0f); // Grow 5% per point of damage? Or 1.0 + (absorbed / 20) -> 1.0 + 0.5
                                                 // for 10 dmg
        // Let's make it more noticeable: +0.2 per 5 damage -> +0.04 per 1 dmg
        scale = 1.0f + (absorbed * 0.05f);

        // Cap max size? Maybe 5x5 max
        scale = Math.min(scale, 5.0f);

        return super.getDimensions(pose).scaled(scale);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (DAMAGE_ABSORBED.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("DamageAbsorbed")) {
            this.dataTracker.set(DAMAGE_ABSORBED, nbt.getFloat("DamageAbsorbed"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("DamageAbsorbed", this.dataTracker.get(DAMAGE_ABSORBED));
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
