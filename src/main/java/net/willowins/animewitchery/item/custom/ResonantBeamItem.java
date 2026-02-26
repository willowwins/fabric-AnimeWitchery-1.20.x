package net.willowins.animewitchery.item.custom;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.networking.ModPackets;
import net.minecraft.text.Text;

public class ResonantBeamItem extends Item {
    private static final int MIN_MANA_COST = 1;
    private static final int MAX_MANA_COST = 500;
    private static final float MIN_DAMAGE = 1.0f;
    private static final float MAX_DAMAGE = 6.0f;
    private static final int RAMP_UP_TICKS = 100; // 5 seconds to max charge
    private static final double RANGE = 120.0; // Quadrupled range
    private static final float MAX_RADIUS = 2.5f; // 5 blocks wide (2.5 radius)

    public ResonantBeamItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK; // Shield-like movement slow down
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.isSneaking()) {
            if (!world.isClient) {
                net.minecraft.nbt.NbtCompound nbt = stack.getOrCreateNbt();
                boolean current = nbt.getBoolean("PreventBreaking");
                nbt.putBoolean("PreventBreaking", !current);

                String state = !current ? "§cDISABLED" : "§aENABLED";
                user.sendMessage(Text.of("§6Resonant Focus Destruction: " + state), true);

                // Play sound
                world.playSound(null, user.getX(), user.getY(), user.getZ(),
                        net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        net.minecraft.sound.SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
            return TypedActionResult.success(stack);
        }
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient)
            return;

        if (user instanceof PlayerEntity player) {
            int ticksActive = getMaxUseTime(stack) - remainingUseTicks;
            float progress = Math.min((float) ticksActive / RAMP_UP_TICKS, 1.0f);

            int currentManaCost = (int) (MIN_MANA_COST + (MAX_MANA_COST - MIN_MANA_COST) * progress);
            float currentDamage = MIN_DAMAGE + (MAX_DAMAGE - MIN_DAMAGE) * progress;
            float currentRadius = progress * MAX_RADIUS;

            IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
            if (mana.getMana() < currentManaCost) {
                player.stopUsingItem();
                return;
            }
            mana.consume(currentManaCost);

            performBeam(world, player, stack, currentDamage, currentRadius, progress);
        }
    }

    // State Tracking (Server-Side Only)
    private static final java.util.Map<java.util.UUID, BeamData> BEAM_STATE = new java.util.HashMap<>();

    private static class BeamData {
        long targetPos = 0;
        int breakProgress = 0;
        int graceTicks = 0;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            BEAM_STATE.remove(player.getUuid());
            // Clear any visual breaking info
            // (We can't easily know the last pos here without the map, but it clears
            // automatically by game eventually or we can check map before removing)
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    private void performBeam(World world, PlayerEntity player, ItemStack stack, float damage, float radius,
            float progress) {
        Vec3d start = player.getEyePos();
        Vec3d look = player.getRotationVector();
        Vec3d end = start.add(look.multiply(RANGE));

        // 1. Block Destruction (Drill Mode) & Range Limiting
        net.minecraft.util.hit.BlockHitResult blockHit = world.raycast(new net.minecraft.world.RaycastContext(
                start, end,
                net.minecraft.world.RaycastContext.ShapeType.COLLIDER,
                net.minecraft.world.RaycastContext.FluidHandling.NONE,
                player));

        if (blockHit.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
            end = blockHit.getPos(); // Update end to stop at block

            // Progressive Breaking Logic
            boolean preventBreaking = stack.getOrCreateNbt().getBoolean("PreventBreaking");
            if (progress > 0.5f && !preventBreaking) {
                net.minecraft.util.math.BlockPos hitPos = blockHit.getBlockPos();
                net.minecraft.block.BlockState hitState = world.getBlockState(hitPos);
                float hardness = hitState.getHardness(world, hitPos);

                BeamData data = BEAM_STATE.computeIfAbsent(player.getUuid(), k -> new BeamData());

                long lastPosLong = data.targetPos;
                long currentPosLong = hitPos.asLong();

                // Check if we are still targeting the same block
                if (currentPosLong == lastPosLong) {
                    // Reset grace ticks
                    data.graceTicks = 0;

                    if (hardness >= 0) { // If breakable
                        data.breakProgress++;

                        // Calculate required time
                        int requiredTicks = (int) (hardness * 4) + 1;
                        if (requiredTicks < 1)
                            requiredTicks = 1;

                        // Send visual break progress
                        int visualStage = (int) (((float) data.breakProgress / requiredTicks) * 9.0f);
                        world.setBlockBreakingInfo(player.getId(), hitPos, visualStage);

                        // Break!
                        if (data.breakProgress >= requiredTicks) {
                            world.breakBlock(hitPos, true, player);

                            // Reset progress and grace
                            data.breakProgress = 0;
                            data.graceTicks = 0;
                            world.setBlockBreakingInfo(player.getId(), hitPos, -1);

                            // AOE Shockwave
                            int breakRadius = 2; // 5x5x5
                            for (int x = -breakRadius; x <= breakRadius; x++) {
                                for (int y = -breakRadius; y <= breakRadius; y++) {
                                    for (int z = -breakRadius; z <= breakRadius; z++) {
                                        if (x == 0 && y == 0 && z == 0)
                                            continue;
                                        net.minecraft.util.math.BlockPos target = hitPos.add(x, y, z);
                                        net.minecraft.block.BlockState s = world.getBlockState(target);
                                        if (s.getHardness(world, target) >= 0
                                                && s.getHardness(world, target) <= hardness + 0.2f) {
                                            world.breakBlock(target, true, player);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Target changed or lost
                    if (lastPosLong != 0) {
                        // Increment Grace Period
                        data.graceTicks++;

                        // Allow 10 ticks (0.5s) of grace before resetting
                        if (data.graceTicks > 10) {
                            // Grace expired, reset
                            world.setBlockBreakingInfo(player.getId(),
                                    net.minecraft.util.math.BlockPos.fromLong(lastPosLong), -1);
                            data.targetPos = currentPosLong;
                            data.breakProgress = 0;
                            data.graceTicks = 0;
                        }
                        // Else: Keep targeting old block in Data, don't update progress
                    } else {
                        // First target
                        data.targetPos = currentPosLong;
                        data.breakProgress = 0;
                        data.graceTicks = 0;
                    }
                }
            }
        } else {
            // Missed block, check grace period
            BeamData data = BEAM_STATE.get(player.getUuid());
            if (data != null && data.targetPos != 0) {
                data.graceTicks++;

                if (data.graceTicks > 10) {
                    world.setBlockBreakingInfo(player.getId(),
                            net.minecraft.util.math.BlockPos.fromLong(data.targetPos), -1);
                    data.targetPos = 0;
                    data.breakProgress = 0;
                    data.graceTicks = 0;
                }
            }
        }

        // 2. AOE Entity Damage (Cylinder)
        Vec3d effectiveEnd = end;
        Box box = player.getBoundingBox().stretch(look.multiply(RANGE)).expand(radius);
        world.getEntitiesByClass(LivingEntity.class, box, e -> e != player && !e.isSpectator() && e.isAlive())
                .forEach(entity -> {
                    // Check distance to entity center (better for tall/wide mobs)
                    if (distanceToLine(entity.getBoundingBox().getCenter(), start, effectiveEnd) <= radius + 1.0) { // Increased
                                                                                                                    // tolerance
                        entity.timeUntilRegen = 0; // Bypass i-frames for continuous beam damage
                        entity.damage(player.getDamageSources().magic(), damage);
                    }
                });

        // Visual impact at hit
        // Sonic Boom removed by user request

        sendParticlePacket(world, start, end, radius, progress);
    }

    private double distanceToLine(Vec3d p, Vec3d start, Vec3d end) {
        Vec3d d = end.subtract(start);
        double l2 = d.lengthSquared();
        if (l2 == 0)
            return p.distanceTo(start);
        double t = Math.max(0, Math.min(1, p.subtract(start).dotProduct(d) / l2));
        Vec3d projection = start.add(d.multiply(t));
        return p.distanceTo(projection);
    }

    private void sendParticlePacket(World world, Vec3d start, Vec3d end, float radius, float progress) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(start.x);
        buf.writeDouble(start.y);
        buf.writeDouble(start.z);
        buf.writeDouble(end.x);
        buf.writeDouble(end.y);
        buf.writeDouble(end.z);
        buf.writeFloat(radius); // Added radius
        buf.writeFloat(progress);

        // Send to all players in range (tracking the chunk)
        // Using player.getWorld().getPlayers() as a simple approximation or ServerWorld
        // helper
        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            for (ServerPlayerEntity p : serverWorld.getPlayers()) {
                // Check distance to avoid sending to far players (simple check, or use
                // tracking)
                if (p.squaredDistanceTo(start) < 128 * 128) {
                    ServerPlayNetworking.send(p, ModPackets.RESONANT_BEAM_PARTICLE, buf);
                }
            }
        }
    }
}
