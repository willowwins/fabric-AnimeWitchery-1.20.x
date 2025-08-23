package net.willowins.animewitchery.effect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.util.ModExplosionManager;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class KamikazeRitualEffect extends StatusEffect {
    
    // Track players who are in the death sequence
    private static final Map<UUID, DeathSequence> activeDeathSequences = new HashMap<>();
    
    // Flag to prevent infinite loops during ritual
    public static boolean isRitualActive = false;
    
    public KamikazeRitualEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
    
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            if (!entity.getWorld().isClient) {
                // Server-side logic
                World world = entity.getWorld();
                BlockPos pos = entity.getBlockPos();
                
                // Check if this player is in a death sequence
                if (activeDeathSequences.containsKey(player.getUuid())) {
                    DeathSequence sequence = activeDeathSequences.get(player.getUuid());
                    sequence.update(world, player);
                    return;
                }
                
                // Spawn ritual particles around the player (server-side)
                for (int i = 0; i < 3; i++) {
                    double angle = (world.getTime() * 0.1) + (i * Math.PI * 2.0 / 3.0);
                    double radius = 2.0 + (amplifier * 0.5);
                    double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
                    double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
                    
                    world.addParticle(ParticleTypes.END_ROD, x, pos.getY() + 0.1, z, 0, 0.1, 0);
                }
                
                // Add some smoke particles for dramatic effect
                if (world.getTime() % 20 == 0) {
                    world.addParticle(ParticleTypes.SMOKE, 
                        pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 3,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 3,
                        0, 0.1, 0);
                }
            } else {
                // Client-side logic - Lodestone particles!
                if (activeDeathSequences.containsKey(player.getUuid())) {
                    spawnLodestoneRitualParticles(player);
                } else {
                    spawnLodestoneAmbientParticles(player, amplifier);
                }
            }
        }
    }
    
    @Override
    public void onRemoved(LivingEntity entity, net.minecraft.entity.attribute.AttributeContainer attributes, int amplifier) {
        // This should never happen since the effect is permanent until death
        super.onRemoved(entity, attributes, amplifier);
    }
    
    // This method will be called when the player dies
    public static boolean onPlayerDeath(PlayerEntity player, DamageSource source) {
        if (player instanceof ServerPlayerEntity serverPlayer && player.hasStatusEffect(ModEffect.KAMIKAZE_RITUAL)) {
            // Cancel the death and start the death sequence
            if (!activeDeathSequences.containsKey(player.getUuid())) {
                DeathSequence sequence = new DeathSequence(player);
                activeDeathSequences.put(player.getUuid(), sequence);
                
                // Set flag to prevent infinite loops
                isRitualActive = true;
                
                // Cancel the death by setting health to max and making invulnerable
                player.setHealth(player.getMaxHealth());
                player.setInvulnerable(true);
                
                // Freeze the player completely
                player.setVelocity(0, 0, 0);
                player.fallDistance = 0;
                player.setSneaking(false);
                player.setSprinting(false);
                
                // Disable movement and actions
                player.setSwimming(false);
                
                // Send dramatic message
                player.getWorld().getServer().getPlayerManager().broadcast(
                    Text.literal("§4§l[FORBIDDEN MAGIC] §c" + player.getName().getString() + " has triggered the Kamikaze Ritual!"),
                    false
                );
                
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cYour soul is forfeit!"));
                
                // Play dramatic kamikaze explosion sound at the start of the countdown
                player.getWorld().playSound(null, player.getBlockPos(), net.minecraft.sound.SoundEvent.of(
                    new net.minecraft.util.Identifier(AnimeWitchery.MOD_ID, "kamikaze_explosion_wind_up")),
                    player.getSoundCategory(), 4.0f, 1f);
                
                return true; // Death was cancelled
            }
        }
        return false; // Death was not cancelled
    }
    
    // Client-side Lodestone particle methods
    private void spawnLodestoneAmbientParticles(PlayerEntity player, int amplifier) {
        BlockPos pos = player.getBlockPos();
        double time = player.getWorld().getTime() * 0.1;
        
        // Rotating ritual aura
        for (int i = 0; i < 6; i++) {
            double angle = time + (i * Math.PI * 2.0 / 6.0);
            double radius = 2.5 + (amplifier * 0.3);
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            
            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                .setColorData(ColorParticleData.create(new java.awt.Color(200, 0, 230), new java.awt.Color(100, 0, 150)).build())
                .setSpinData(SpinParticleData.create(0.1f, 0.2f).build())
                .setLifetime(40)
                .setScaleData(GenericParticleData.create(0.1f, 0.2f, 0.0f).build())
                .addMotion(0, 0.05, 0)
                .spawn(player.getWorld(), x, pos.getY() + 0.2, z);
        }
    }
    
    private void spawnLodestoneRitualParticles(PlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        double time = player.getWorld().getTime() * 0.2;
        
        // Expanding ritual circle
        for (int i = 0; i < 12; i++) {
            double angle = time + (i * Math.PI * 2.0 / 12.0);
            double radius = 4.0 + Math.sin(time * 2) * 2.0;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            
            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                .setColorData(ColorParticleData.create(new java.awt.Color(230, 0, 25), new java.awt.Color(150, 0, 0)).build())
                .setSpinData(SpinParticleData.create(0.3f, 0.5f).build())
                .setLifetime(60)
                .setScaleData(GenericParticleData.create(0.2f, 0.4f, 0.0f).build())
                .addMotion(0, 0.1, 0)
                .spawn(player.getWorld(), x, pos.getY() + 0.1, z);
        }
        
        // Energy beams shooting outward
        if (player.getWorld().getTime() % 15 == 0) {
            for (int i = 0; i < 8; i++) {
                double angle = (i * Math.PI * 2.0 / 8.0);
                double x = pos.getX() + 0.5 + Math.cos(angle) * 15.0;
                double z = pos.getZ() + 0.5 + Math.sin(angle) * 15.0;
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setColorData(ColorParticleData.create(new java.awt.Color(255, 0, 0), new java.awt.Color(200, 0, 0)).build())
                .setLifetime(30)
                .setScaleData(GenericParticleData.create(0.1f, 0.05f, 0.0f).build())
                .addMotion((x - pos.getX() - 0.5) * 0.1, 0, (z - pos.getZ() - 0.5) * 0.1)
                .spawn(player.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            }
        }
    }
    
    // Inner class to handle the death sequence
    private static class DeathSequence {
        private final PlayerEntity player;
        private final BlockPos deathPos;
        private int ticks = 0;
        private final int TOTAL_TICKS = 200; // 10 seconds at 20 ticks/second

        public DeathSequence(PlayerEntity player) {
            this.player = player;
            this.deathPos = player.getBlockPos();
        }

        public void update(World world, PlayerEntity player) {
            ticks++;

            // Keep player completely frozen in place
            player.setPosition(deathPos.getX() + 0.5, deathPos.getY() + 0.5, deathPos.getZ() + 0.5);
            player.setVelocity(0, 0, 0);
            player.fallDistance = 0;

            // Keep health at max and maintain invulnerability
            player.setHealth(player.getMaxHealth());
            player.setInvulnerable(true);

            // Disable all movement and actions
            player.setSneaking(false);
            player.setSprinting(false);
            player.setSwimming(false);

            // Add dramatic particles
            double progress = (double) ticks / TOTAL_TICKS;

            // Rotating ritual circle
            for (int i = 0; i < 8; i++) {
                double angle = (world.getTime() * 0.2) + (i * Math.PI * 2.0 / 8.0);
                double radius = 3.0 + (progress * 5.0); // Expanding circle
                double x = deathPos.getX() + 0.5 + Math.cos(angle) * radius;
                double z = deathPos.getZ() + 0.5 + Math.sin(angle) * radius;

                world.addParticle(ParticleTypes.END_ROD, x, deathPos.getY() + 0.1, z, 0, 0.1, 0);
            }

            // Energy beams
            if (ticks % 10 == 0) {
                for (int i = 0; i < 4; i++) {
                    double angle = (i * Math.PI * 2.0 / 4.0);
                    double x = deathPos.getX() + 0.5 + Math.cos(angle) * 10.0;
                    double z = deathPos.getZ() + 0.5 + Math.sin(angle) * 10.0;

                    world.addParticle(ParticleTypes.FIREWORK, x, deathPos.getY() + 0.5, z, 0, 0.5, 0);
                }
            }

            // Countdown messages
            // if (ticks == 40) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c9 seconds remaining..."));
            // if (ticks == 60) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c8 seconds remaining..."));
            // if (ticks == 80) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c7 seconds remaining..."));
            // if (ticks == 100) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c6 seconds remaining..."));
            // if (ticks == 120) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c5 seconds remaining..."));
            // if (ticks == 140) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c4 seconds remaining..."));
            // if (ticks == 160) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c3 seconds remaining..."));
            // if (ticks == 180) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c2 seconds remaining..."));
            // if (ticks == 190) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c1 second remaining..."));
            
            // Final countdown
            if (ticks == 200) {
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cRITUAL COMPLETE!"));
                player.getWorld().playSound(null, player.getBlockPos(), net.minecraft.sound.SoundEvent.of(
                                new net.minecraft.util.Identifier(AnimeWitchery.MOD_ID, "kamikaze_explosion")),
                        player.getSoundCategory(), 4.0f, 1f);
            }
            
            // Execute the ritual
            if (ticks >= TOTAL_TICKS) {
                executeRitual(world, player);
                activeDeathSequences.remove(player.getUuid());
                isRitualActive = false; // Reset flag after ritual completes
            }
        }
        
        private void executeRitual(World world, PlayerEntity player) {
            // Start our HOMEMADE SPHERICAL EXPLOSION sequence! 🔥💥
            HomemadeExplosion explosion = new HomemadeExplosion(world, deathPos, 100.0, player);
            explosion.start(); // no recursive scheduling anymore

            // Play explosion sound
            world.playSound(null, deathPos, SoundEvents.ENTITY_GENERIC_EXPLODE, 
                player.getSoundCategory(), 4.0f, 0.8f);
            
            // Ban the player
            try {
                String banCommand = "ban " + player.getName().getString() + " [Kamikaze Ritual: Forbidden Magic]";
                world.getServer().getCommandManager().executeWithPrefix(
                    world.getServer().getCommandSource(), banCommand
                );
                
                world.getServer().getPlayerManager().broadcast(
                    Text.literal("§4§l[FORBIDDEN MAGIC] §c" + player.getName().getString() + " has been erased from this world!"),
                    false
                );
                
            } catch (Exception e) {
                AnimeWitchery.LOGGER.error("Failed to ban player after Kamikaze Ritual: " + e.getMessage());
            }
        }
        
        // Custom explosion class for maximum destruction control!
        private static class HomemadeExplosion implements ModExplosionManager.TickableExplosion {
            private final World world;
            private final BlockPos center;
            private final double maxRadius;
            private final PlayerEntity player;

            private int tick = 0;
            private final int EXPANSION_TICKS = 200;

            private double currentRadius = 0.0;
            private double lastRadius = 0.0;
            private double currentRadiusSq = 0.0;
            private double lastRadiusSq = 0.0;

            // Performance knobs
            private static final int MAX_BLOCKS_PER_TICK = 5000; // tune for your server
            private static final int MAX_PARTICLES_PER_TICK = 400; // throttle visuals
            private static final int SCAN_STRIDE = 1; // step size in blocks
            private static final int DILATION_BUDGET_PER_TICK = 5000;   // seals micro gaps around destroyed blocks
            private int dilationBudget = DILATION_BUDGET_PER_TICK;
            private int dynamicStride = 1;          // adaptive scan stride per tick
            private double shellMargin = 2.0;       // margin added to inner/outer radii each tick


            // Resumable scan state
            private int scanRadius = 0;     // ceil(currentRadius)
            private int sx = Integer.MIN_VALUE, sy = Integer.MIN_VALUE, sz = Integer.MIN_VALUE;
            private int minY, maxY;

            public HomemadeExplosion(World world, BlockPos center, double maxRadius, PlayerEntity player) {
                this.world = world;
                this.center = center.toImmutable();
                this.maxRadius = maxRadius;
                this.player = player;
                initYBounds();
            }

            // Adaptive stride: fine near center, coarser far out (tune to taste)
            private int strideFor(double r) {
                return 1; // Always stride=1 for clarity
            }

            // Shell margin ≳ √3 * stride to avoid voxel holes; a bit more is fine
            private double marginFor(int stride) {
                return Math.max(2.0, 1.9 * stride);
            }

            // Stable per-block hash → [0,1)
            private static double hash01(int x, int y, int z, int salt) {
                long h = 1469598103934665603L;
                h ^= (long)x * 0x8da6b343L; h *= 0xff51afd7ed558ccdl;
                h ^= (long)y * 0xd8163841L; h *= 0xc4ceb9fe1a85ec53l;
                h ^= (long)z * 0xcb1ab31fL; h ^= (long)salt * 0x9e3779b97f4a7c15L;
                h ^= (h >>> 33); h *= 0xff51afd7ed558ccdl;
                h ^= (h >>> 33); h *= 0xc4ceb9fe1a85ec53l;
                h ^= (h >>> 33);
                return (h >>> 11) * (1.0 / (1L << 53));
            }

            // Ease for tapering probability (smooth near inner edge, strong near front)
            private static double easeOutCubic(double t) {
                double u = 1.0 - Math.max(0.0, Math.min(1.0, t));
                return 1.0 - u*u*u;
            }

            // Small neighbor dilation to seal tiny holes without heavy cost
            private void maybeDilateAt(BlockPos pos, double edgeRadiusSq) {
                if (dilationBudget <= 0) return;
                final BlockPos[] nbs = new BlockPos[] {
                    pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()
                };
                for (BlockPos n : nbs) {
                    if (dilationBudget <= 0) break;
                    if (!world.isChunkLoaded(n)) continue;
                    var st = world.getBlockState(n);
                    if (st.isAir() || st.getHardness(world, n) < 0) continue;
                    int dx = n.getX() - center.getX();
                    int dy = n.getY() - center.getY();
                    int dz = n.getZ() - center.getZ();
                    double d2 = (double)dx*dx + (double)dy*dy + (double)dz*dz;
                    if (d2 <= edgeRadiusSq) {
                        world.setBlockState(n, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                        dilationBudget--;
                    }
                }
            }


            private void initYBounds() {
                minY = Math.max(world.getBottomY(), center.getY() - (int)Math.ceil(maxRadius));
                maxY = Math.min(world.getTopY() - 1, center.getY() + (int)Math.ceil(maxRadius));
            }

            public void start() { ModExplosionManager.add(this); }

            public static void sendKamikazeFx(ServerWorld world,
                                              BlockPos center,
                                              double currentRadius,
                                              double maxRadius,
                                              double progress,           // 0..1
                                              int seed,
                                              boolean bigPulse) {
                Vec3d c = Vec3d.ofCenter(center);
                double reach = Math.max(96.0, currentRadius + 64.0); // who should see the FX

                for (ServerPlayerEntity p : PlayerLookup.around(world, c, reach)) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(c.x); buf.writeDouble(c.y); buf.writeDouble(c.z);
                    buf.writeFloat((float) currentRadius);
                    buf.writeFloat((float) maxRadius);
                    buf.writeFloat((float) progress);
                    buf.writeInt(seed);
                    buf.writeBoolean(bigPulse);
                    ServerPlayNetworking.send(p, ModPackets.KAMIKAZE_FX, buf);
                }
            }


            @Override
            public boolean tickOnce(net.minecraft.server.MinecraftServer server) {
                if (world.isClient()) return true;
            
                tick++;
                double progress = (double) tick / EXPANSION_TICKS;
                if (progress > 1.0) progress = 1.0;
            
                lastRadius = currentRadius;
                lastRadiusSq = lastRadius * lastRadius;
            
                currentRadius = progress * maxRadius;
                currentRadiusSq = currentRadius * currentRadius;

                boolean bigPulse = (tick % 12) == 0; // a nice “beat”
                int seed = (int)(world.getTime() ^ tick ^ center.asLong());

                if (world instanceof net.minecraft.server.world.ServerWorld sw) {
                    sendKamikazeFx(sw, center, currentRadius, maxRadius, progress, seed, bigPulse);
                }


                // --- NEW: adaptive stride & shell margins per tick ---
                this.dynamicStride = strideFor(currentRadius);
                this.shellMargin = marginFor(this.dynamicStride);
            
                // Reset dilation budget each tick
                this.dilationBudget = DILATION_BUDGET_PER_TICK;
            
                scanRadius = (int)Math.ceil(currentRadius + shellMargin);
            
                if (tick % 20 == 0) {
                    AnimeWitchery.LOGGER.info("Explosion tick {}/{} radius {:.1f}/{} ({:.0f}%)",
                            tick, EXPANSION_TICKS, currentRadius, maxRadius, progress * 100.0);
                }
            
                int changed = destroyIncrementalShell();  // ← uses new edge logic
                killEntitiesInShell();
                spawnThrottledParticles();
            
                if (changed > 0) {
                    world.playSound(null, center, SoundEvents.BLOCK_GLASS_BREAK,
                            player.getSoundCategory(), 0.25f, 1.0f + (float)(progress * 0.5f));
                }
            
                boolean finishedRadius = tick >= EXPANSION_TICKS;
                boolean finishedScan = (sx == Integer.MIN_VALUE);
                return finishedRadius && finishedScan;
            }
            
            private void killEntitiesInShell() {
                if (world.isClient()) return;
            
                // Thin shell thickness — keeps the work focused near the expansion front
                final double shellThickness = Math.max(1.5, currentRadius - lastRadius + 1.0);
            
                // Bounding box just big enough for the current radius
                int r = (int) Math.ceil(currentRadius + shellThickness + 1);
                var aabb = new net.minecraft.util.math.Box(
                    center.getX() - r, center.getY() - r, center.getZ() - r,
                    center.getX() + r + 1, center.getY() + r + 1, center.getZ() + r + 1
                );
            
                final var centerVec = new net.minecraft.util.math.Vec3d(
                    center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5
                );
            
                // Fetch candidates once; filter by distance to keep only the shell
                var entities = world.getOtherEntities(
                    null,
                    aabb,
                    e -> {
                        // Skip removed/immune players (spectator/creative), and the ritual source if you wish
                        if (!e.isAlive()) return false;
                        if (e instanceof net.minecraft.server.network.ServerPlayerEntity sp) {
                            if (sp.isSpectator() || sp.isCreative()) return false;
                            // Optionally: don't kill the caster while the ritual animation runs
                            if (sp.getUuid().equals(player.getUuid())) return false;
                        }
                        // Distance check (squared)
                        double d2 = e.getPos().squaredDistanceTo(centerVec);
                        return d2 > lastRadiusSq && d2 <= currentRadiusSq;
                    }
                );
            
                for (var e : entities) {
                    // Non-living: just remove (FallingBlockEntity, ItemEntity, projectiles, vehicles, TNT, etc.)
                    if (!(e instanceof net.minecraft.entity.LivingEntity le)) {
                        e.discard();
                        continue;
                    }
            
                    // Living: deal overwhelming explosion damage and small knockback if desired
                    var src = world.getDamageSources().explosion(player, player);
                    le.damage(src, Float.MAX_VALUE);
                    // Optional flair:
                    le.setFireTicks(60);
                    // tiny outward push to sell the effect (won’t matter if it dies instantly)
                    var dir = e.getPos().subtract(centerVec).normalize();
                    e.addVelocity(dir.x * 0.6, 0.3, dir.z * 0.6);
                    e.velocityDirty = true;
                }
            }
            

            /**
             * Destroys only blocks with (lastRadius .. currentRadius], shaped by:
             * - strong band near the frontier (~98% certainty)
             * - taper band behind it (30%→95%)
             * - stable jitter on the edge radius for organic look
             * Uses resumable nested loops and a per-tick budget.
             */
            private int destroyIncrementalShell() {
                int budget = MAX_BLOCKS_PER_TICK;
                int destroyed = 0;

                // Prepare squared bounds once (with margins) for quick outer reject
                double lastSqWithMargin    = Math.max(0.0, (lastRadius  - shellMargin) * (lastRadius  - shellMargin));
                double currentSqWithMargin = (currentRadius + shellMargin) * (currentRadius + shellMargin);

                // Initialize cursors the first time or after finishing a sweep
                if (sx == Integer.MIN_VALUE) {
                    sx = -scanRadius; sy = minY - center.getY(); sz = -scanRadius;
                }

                for (int x = sx; x <= scanRadius; x += dynamicStride) {
                    for (int yOff = sy; yOff <= (maxY - center.getY()); yOff += dynamicStride) {
                        int y = center.getY() + yOff;
                        for (int z = sz; z <= scanRadius; z += dynamicStride) {
                            if (budget <= 0) {
                                // Save cursors and resume next tick from here
                                sx = x; sy = yOff; sz = z;
                                return destroyed;
                            }

                            int dx = x, dz = z;
                            double distSq = (double)dx*dx + (double)yOff*yOff + (double)dz*dz;

                            // Outer quick reject using margin-expanded bounds
                            if (distSq <= lastSqWithMargin || distSq > currentSqWithMargin) continue;

                            BlockPos pos = center.add(dx, yOff, dz);
                            var state = world.getBlockState(pos);
                            if (state.isAir() || state.getHardness(world, pos) < 0) continue;

                            // ----- Edge shaping -----
                            // Stable jitter so the frontier isn't a perfect sphere
                            double jitterAmp = Math.max(0.75, 0.35 * dynamicStride);                // few tenths of a block
                            double jitter = (hash01(center.getX()+dx, y, center.getZ()+dz, 1337) * 2.0 - 1.0) * jitterAmp;
                            double rEdge = currentRadius + jitter;

                            // Two bands: strong (thin) and taper (thicker)
                            double strongBand = Math.max(1.0, shellMargin * 0.60);
                            double taperBand  = Math.max(2.0, shellMargin * 1.75);

                            double rStrongInner = Math.max(0.0, rEdge - strongBand);
                            double rTaperInner  = Math.max(0.0, rEdge - strongBand - taperBand);

                            double rEdgeSq        = rEdge * rEdge;
                            double rStrongInnerSq = rStrongInner * rStrongInner;
                            double rTaperInnerSq  = rTaperInner  * rTaperInner;

                            // Only process cells the frontier actually reached this tick (with jitter)
                            if (distSq <= rEdgeSq) {
                                // 1) Strong band: almost guaranteed destruction, but not 100%
                                if (distSq > rStrongInnerSq) {
                                    double p = 0.98; // 98% to avoid razor-sharp wall
                                    if (hash01(pos.getX(), pos.getY(), pos.getZ(), 4242) <= p) {
                                        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                                        destroyed++; budget--;
                                        // Seal micro gaps cheaply
                                        maybeDilateAt(pos, rEdgeSq);
                                    }
                                    continue;
                                }

                                // 2) Taper band: probability falls off smoothly behind the front
                                if (distSq > rTaperInnerSq) {
                                    double dist = Math.sqrt(distSq);
                                    double t = (rStrongInner <= rTaperInner) ? 1.0
                                            : (dist - rTaperInner) / (rStrongInner - rTaperInner);
                                    t = Math.max(0.0, Math.min(1.0, t));
                                    double bias = easeOutCubic(t);   // 0 inner → 1 near strong
                                    double p = 0.30 + 0.65 * bias;   // 0.30 → 0.95
                                    // Slight global falloff so far shells are a hair lighter
                                    p *= (1.0 - (currentRadius / maxRadius) * 0.15);

                                    if (hash01(pos.getX(), pos.getY(), pos.getZ(), 7777) <= p) {
                                        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                                        destroyed++; budget--;
                                    }
                                    continue;
                                }

                                // 3) Deeper than taper band: leave for later sweeps (keeps edge organic).
                            }
                        }
                        sz = -scanRadius; // reset z for next row
                    }
                    sy = minY - center.getY(); // reset y for next column
                }

                // Finished the sweep for this radius
                sx = sy = sz = Integer.MIN_VALUE;
                return destroyed;
            }


            private void spawnThrottledParticles() {
                // Keep visuals reasonable; more near the front
                int particleBudget = Math.min((int)(currentRadius * 6), MAX_PARTICLES_PER_TICK);

                for (int i = 0; i < particleBudget; i++) {
                    double phi = world.random.nextDouble() * Math.PI * 2.0;
                    double theta = world.random.nextDouble() * Math.PI;

                    double r = currentRadius; // shell surface
                    double x = center.getX() + 0.5 + r * Math.sin(theta) * Math.cos(phi);
                    double y = center.getY() + 0.5 + r * Math.cos(theta);
                    double z = center.getZ() + 0.5 + r * Math.sin(theta) * Math.sin(phi);

                    world.addParticle(
                            (currentRadius < maxRadius * 0.4) ? net.minecraft.particle.ParticleTypes.FLAME
                                    : (currentRadius < maxRadius * 0.8) ? net.minecraft.particle.ParticleTypes.SMOKE
                                    : net.minecraft.particle.ParticleTypes.EXPLOSION,
                            x, y, z, 0, 0.05, 0
                    );
                }
            }
        }
    }
}
