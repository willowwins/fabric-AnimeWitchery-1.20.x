package net.willowins.animewitchery.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.util.ModExplosionManager;

public class ResonantSparkEntity extends ThrownItemEntity {

    public ResonantSparkEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    public ResonantSparkEntity(World world, LivingEntity owner) {
        super(ModEntities.RESONANT_SPARK, owner, world);
        this.setNoGravity(true);
    }

    public ResonantSparkEntity(World world, double x, double y, double z) {
        super(ModEntities.RESONANT_SPARK, x, y, z, world);
        this.setNoGravity(true);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RESONANT_SPARK;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        // Maintain constant velocity (counteract drag)
        if (!this.hasNoGravity()) {
            this.setNoGravity(true);
        }

        net.minecraft.util.math.Vec3d currentVel = this.getVelocity();
        // If moving, normalize and scale to constant speed (e.g. 0.5F which was the
        // throw speed)
        // actually throw speed was 0.5F force + velocity...
        // Let's just multiply by (1/0.99) to counter the default 0.99 drag if we want
        // to keep it simple,
        // or stricter: force exact speed.
        // User said "maintain velocity without just relying on throw force".
        // This implies it should keep going.

        if (!this.getWorld().isClient && currentVel.lengthSquared() > 0) {
            // Apply a small constant propulsion if it gets too slow, or just remove drag.
            // ThrownItemEntity applies 0.99 drag.
            // We can multiply by ~1.01 to cancel it out.
            this.setVelocity(currentVel.multiply(1.01010101));
            if (this.age > 200) { // Safety timeout
                this.discard();
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        // Absolute safety check: Never explode on owner
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if (entityHit.getEntity() == this.getOwner()) {
                return;
            }
        }

        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            // Trigger explosion at impact point
            BlockPos impactPos = BlockPos.ofFloored(hitResult.getPos());
            SparkExplosion explosion = new SparkExplosion(this.getWorld(), impactPos, 20.0,
                    this.getOwner() instanceof net.minecraft.entity.player.PlayerEntity p ? p : null);
            explosion.start();

            // Play sound
            this.getWorld().playSound(null, impactPos, SoundEvents.ENTITY_GENERIC_EXPLODE,
                    this.getSoundCategory(), 4.0f,
                    (1.0f + (this.getWorld().random.nextFloat() - this.getWorld().random.nextFloat()) * 0.2f) * 0.7f);

            this.discard();
        }
    }

    // Custom Explosion Logic (Scaled down Kamikaze)
    private static class SparkExplosion implements ModExplosionManager.TickableExplosion {
        private final World world;
        private final BlockPos center;
        private final double maxRadius;
        private final net.minecraft.entity.player.PlayerEntity player; // Can be null

        private int tick = 0;
        private final int EXPANSION_TICKS = 20; // 1 second expansion

        private double currentRadius = 0.0;

        public SparkExplosion(World world, BlockPos center, double maxRadius,
                net.minecraft.entity.player.PlayerEntity player) {
            this.world = world;
            this.center = center;
            this.maxRadius = maxRadius;
            this.player = player;
        }

        public void start() {
            ModExplosionManager.add(this);
        }

        @Override
        public boolean tickOnce(net.minecraft.server.MinecraftServer server) {
            if (world.isClient())
                return true;

            tick++;
            double progress = (double) tick / EXPANSION_TICKS;
            if (progress > 1.0)
                progress = 1.0;

            currentRadius = progress * maxRadius;

            // Destroy blocks
            if (world instanceof ServerWorld sw) {
                int r = (int) Math.ceil(currentRadius);
                int rPrevCalc = (int) Math.ceil((progress - (1.0 / EXPANSION_TICKS)) * maxRadius);
                if (rPrevCalc < 0)
                    rPrevCalc = 0;
                final int rPrev = rPrevCalc;

                // Only scan the shell
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        for (int z = -r; z <= r; z++) {
                            double distSq = x * x + y * y + z * z;
                            if (distSq <= r * r && distSq > rPrev * rPrev) {
                                BlockPos pos = center.add(x, y, z);
                                if (world.getBlockState(pos).isAir())
                                    continue;

                                // 90% chance to break weak blocks, less for strong
                                float hardness = world.getBlockState(pos).getHardness(world, pos);
                                if (hardness >= 0 && hardness < 50) {
                                    if (world.getRandom().nextFloat() < 0.9f) {
                                        if (player != null) {
                                            world.breakBlock(pos, false, player);
                                        } else {
                                            world.breakBlock(pos, false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                net.minecraft.util.math.Box box = new net.minecraft.util.math.Box(center).expand(currentRadius);
                world.getOtherEntities(null, box,
                        e -> e.squaredDistanceTo(center.getX(), center.getY(), center.getZ()) <= currentRadius
                                * currentRadius
                                && e.squaredDistanceTo(center.getX(), center.getY(), center.getZ()) > rPrev * rPrev)
                        .forEach(e -> {
                            if (e instanceof LivingEntity le) {
                                if (player != null && le == player)
                                    return;
                                le.damage(world.getDamageSources().explosion(player, player), 50.0f);
                                le.addVelocity(
                                        (e.getX() - center.getX()) * 0.5,
                                        0.5,
                                        (e.getZ() - center.getZ()) * 0.5);
                            }
                        });

                sw.spawnParticles(ParticleTypes.EXPLOSION, center.getX(), center.getY(), center.getZ(), 10,
                        currentRadius, currentRadius, currentRadius, 0.1);
            }

            return tick >= EXPANSION_TICKS;
        }
    }
}
