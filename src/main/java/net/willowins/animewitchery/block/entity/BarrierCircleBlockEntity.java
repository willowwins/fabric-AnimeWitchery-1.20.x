package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.client.sky.SkyRitualRenderer;
import net.willowins.animewitchery.ritual.RitualConfiguration;
import net.willowins.animewitchery.ritual.RitualConfigurationBuilder;

import java.util.*;

public class BarrierCircleBlockEntity extends BlockEntity {
    private CircleStage stage = CircleStage.BASIC; // BASIC -> DEFINED -> COMPLETE
    private CircleType circleType = CircleType.NONE;
    private int ritualActivationStep = 0; // 0=none, 1=east, 2=south, 3=west
    private long step3StartTime = 0; // Track when step 3 started for energy ball timing
    private boolean ritualActive = false; // Track if ritual is currently active
    private long lastIntegrityCheck = 0; // Track last integrity check time
    
    // Barrier durability settings
    private int barrierMaxDurability = 1000;
    private int barrierDurability = 1000;
    private int barrierRegenPerSecond = 5;
    private long lastRegenTick = 0;
    private long lastEffectTick = 0;
    
    // Barrier distance storage (from distance glyphs)
    private int northDistance = 5;
    private int southDistance = 5;
    private int eastDistance = 5;
    private int westDistance = 5;
    
    // Cached glyph positions (stored when ritual is activated)
    private BlockPos northGlyphPos = null;
    private BlockPos southGlyphPos = null;
    private BlockPos eastGlyphPos = null;
    private BlockPos westGlyphPos = null;

    // Whitelist of players allowed to cross the barrier
    private final Set<UUID> allowedPlayerUuids = new HashSet<>();
    
    // Ritual configuration based on obelisk variants
    private RitualConfiguration ritualConfiguration = null;
    
    public enum CircleStage {
        BASIC,      // Just created - shows basic outline
        DEFINED,    // Type set with Barrier Catalyst
        COMPLETE    // Fully drawn circle
    }
    
    public enum CircleType {
        NONE,
        BARRIER     // For obelisk placement
    }

    // Global registry of loaded barriers
    private static final Set<BarrierCircleBlockEntity> LOADED_BARRIERS =
            Collections.newSetFromMap(new WeakHashMap<>());

    public BarrierCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY, pos, state);
        LOADED_BARRIERS.add(this);
    }
    
    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        
        // Register with sky renderer on client side if ritual is active
        if (world != null && world.isClient && isRitualActive()) {
            System.out.println("BarrierCircleBlockEntity.setWorld: Registering with sky renderer at " + pos + " (ritualActive: " + ritualActive + ", step: " + ritualActivationStep + ")");
            SkyRitualRenderer.addActiveRitual(pos);
        } else {
            System.out.println("BarrierCircleBlockEntity.setWorld: Not registering with sky renderer - world: " + (world != null) + ", isClient: " + (world != null && world.isClient) + ", isRitualActive: " + isRitualActive());
        }
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
        nbt.putInt("northDistance", northDistance);
        nbt.putInt("southDistance", southDistance);
        nbt.putInt("eastDistance", eastDistance);
        nbt.putInt("westDistance", westDistance);
        nbt.putInt("barrierMaxDurability", barrierMaxDurability);
        nbt.putInt("barrierDurability", barrierDurability);
        nbt.putInt("barrierRegenPerSecond", barrierRegenPerSecond);
        nbt.putLong("lastRegenTick", lastRegenTick);
        
        // Save cached glyph positions
        if (northGlyphPos != null) {
            nbt.putLong("northGlyphPos", northGlyphPos.asLong());
        }
        if (southGlyphPos != null) {
            nbt.putLong("southGlyphPos", southGlyphPos.asLong());
        }
        if (eastGlyphPos != null) {
            nbt.putLong("eastGlyphPos", eastGlyphPos.asLong());
        }
        if (westGlyphPos != null) {
            nbt.putLong("westGlyphPos", westGlyphPos.asLong());
        }

        // Save allowed players
        NbtCompound allowed = new NbtCompound();
        int index = 0;
        for (UUID id : allowedPlayerUuids) {
            allowed.putUuid("u" + index++, id);
        }
        allowed.putInt("count", index);
        nbt.put("allowedPlayers", allowed);
        
        // Save ritual configuration
        if (ritualConfiguration != null) {
            NbtCompound ritualNbt = new NbtCompound();
            ritualNbt.putString("type", ritualConfiguration.getRitualType().name());
            if (ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.BARRIER) {
                ritualNbt.putString("shape", ritualConfiguration.getBarrierShape().name());
            } else if (ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
                ritualNbt.putString("effect", ritualConfiguration.getEffectType().name());
            }
            ritualNbt.putInt("strength", ritualConfiguration.getStrength());
            ritualNbt.putInt("regenRate", ritualConfiguration.getRegenRate());
            nbt.put("ritualConfiguration", ritualNbt);
        }
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
        this.northDistance = nbt.getInt("northDistance");
        this.southDistance = nbt.getInt("southDistance");
        this.eastDistance = nbt.getInt("eastDistance");
        this.westDistance = nbt.getInt("westDistance");
        if (nbt.contains("barrierMaxDurability")) this.barrierMaxDurability = nbt.getInt("barrierMaxDurability");
        if (nbt.contains("barrierDurability")) this.barrierDurability = nbt.getInt("barrierDurability");
        if (nbt.contains("barrierRegenPerSecond")) this.barrierRegenPerSecond = nbt.getInt("barrierRegenPerSecond");
        if (nbt.contains("lastRegenTick")) this.lastRegenTick = nbt.getLong("lastRegenTick");
        
        // Load cached glyph positions
        if (nbt.contains("northGlyphPos")) {
            this.northGlyphPos = BlockPos.fromLong(nbt.getLong("northGlyphPos"));
        }
        if (nbt.contains("southGlyphPos")) {
            this.southGlyphPos = BlockPos.fromLong(nbt.getLong("southGlyphPos"));
        }
        if (nbt.contains("eastGlyphPos")) {
            this.eastGlyphPos = BlockPos.fromLong(nbt.getLong("eastGlyphPos"));
        }
        if (nbt.contains("westGlyphPos")) {
            this.westGlyphPos = BlockPos.fromLong(nbt.getLong("westGlyphPos"));
        }
        
        // Load allowed players
        this.allowedPlayerUuids.clear();
        if (nbt.contains("allowedPlayers")) {
            NbtCompound allowed = nbt.getCompound("allowedPlayers");
            int count = allowed.getInt("count");
            for (int i = 0; i < count; i++) {
                String key = "u" + i;
                if (allowed.containsUuid(key)) {
                    this.allowedPlayerUuids.add(allowed.getUuid(key));
                }
            }
        }
        
        // Load ritual configuration
        if (nbt.contains("ritualConfiguration")) {
            NbtCompound ritualNbt = nbt.getCompound("ritualConfiguration");
            RitualConfiguration.RitualType type = RitualConfiguration.RitualType.valueOf(ritualNbt.getString("type"));
            int strength = ritualNbt.getInt("strength");
            int regenRate = ritualNbt.getInt("regenRate");
            
            if (type == RitualConfiguration.RitualType.BARRIER) {
                RitualConfiguration.BarrierShape shape = RitualConfiguration.BarrierShape.valueOf(ritualNbt.getString("shape"));
                this.ritualConfiguration = new RitualConfiguration(type, shape, null, strength, regenRate, new HashMap<>());
            } else if (type == RitualConfiguration.RitualType.EFFECT) {
                RitualConfiguration.EffectType effect = RitualConfiguration.EffectType.valueOf(ritualNbt.getString("effect"));
                this.ritualConfiguration = new RitualConfiguration(type, null, effect, strength, regenRate, new HashMap<>());
            }
        }
        
        // Register with sky renderer on client side if ritual is active
        if (world != null && world.isClient && isRitualActive()) {
            System.out.println("BarrierCircleBlockEntity.readNbt: Registering with sky renderer at " + pos + " (ritualActive: " + ritualActive + ", step: " + ritualActivationStep + ")");
            SkyRitualRenderer.addActiveRitual(pos);
        } else {
            System.out.println("BarrierCircleBlockEntity.readNbt: Not registering with sky renderer - world: " + (world != null) + ", isClient: " + (world != null && world.isClient) + ", isRitualActive: " + isRitualActive());
        }
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
            // On the server, capture allowed players when barrier becomes active
            if (!world.isClient) {
                captureAllowedPlayers();
                // Build ritual configuration when ritual becomes fully active
                if (!buildRitualConfiguration()) {
                    // If ritual configuration is invalid, deactivate the ritual
                    System.out.println("BarrierCircle: Invalid ritual configuration - deactivating ritual!");
                    deactivateRitual();
                    return;
                }
            }
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
    
    /**
     * Builds the ritual configuration based on the obelisk variants around this barrier circle
     * Returns true if configuration is valid, false if invalid
     */
    private boolean buildRitualConfiguration() {
        if (world == null || world.isClient) return false;
        
        RitualConfiguration config = RitualConfigurationBuilder.buildConfiguration(world, pos);
        if (RitualConfigurationBuilder.isValidConfiguration(config)) {
            this.ritualConfiguration = config;
            System.out.println("BarrierCircle: Built ritual configuration: " + config);
            
            // Apply configuration to barrier properties
            applyRitualConfiguration(config);
            return true;
        } else {
            System.out.println("BarrierCircle: Invalid ritual configuration - ritual cannot activate!");
            return false;
        }
    }
    
    /**
     * Applies the ritual configuration to modify barrier properties
     */
    private void applyRitualConfiguration(RitualConfiguration config) {
        if (config == null) return;
        
        if (config.getRitualType() == RitualConfiguration.RitualType.BARRIER) {
            // Barrier mode: Apply barrier properties
            int strengthMultiplier = config.getStrength();
            int newMaxDurability = 1000 * strengthMultiplier;
            setBarrierMaxDurability(newMaxDurability);
            this.barrierDurability = newMaxDurability; // Reset to full
            
            int regenMultiplier = config.getRegenRate();
            int newRegenRate = 5 * regenMultiplier;
            setBarrierRegenPerSecond(newRegenRate);
            
            System.out.println("BarrierCircle: Applied barrier configuration - Durability: " + newMaxDurability + ", Regen: " + newRegenRate + "/s");
            System.out.println("BarrierCircle: Barrier shape: " + config.getBarrierShape());
            
        } else if (config.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
            // Effect mode: Apply effect properties
            int strengthMultiplier = config.getStrength();
            int durationMultiplier = config.getRegenRate(); // In effect mode, regenRate represents duration
            
            System.out.println("BarrierCircle: Applied effect configuration - Effect: " + config.getEffectType() + ", Strength: " + strengthMultiplier + ", Duration: " + durationMultiplier);
            
            // TODO: Implement effect application logic
            // For now, just set barrier to minimal values since we're in effect mode
            setBarrierMaxDurability(100);
            this.barrierDurability = 100;
            setBarrierRegenPerSecond(0);
        }
    }
    
    public long getStep3StartTime() {
        return step3StartTime;
    }
    
    public boolean isRitualActive() {
        return ritualActive && ritualActivationStep >= 3;
    }
    
    public boolean isBarrierFunctional() {
        return isRitualActive() && barrierDurability > 0;
    }
    
    public void setRitualActive(boolean active) {
        this.ritualActive = active;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public int getBarrierDurability() { return barrierDurability; }
    public int getBarrierMaxDurability() { return barrierMaxDurability; }
    public void setBarrierMaxDurability(int max) { this.barrierMaxDurability = Math.max(1, max); markDirty(); }
    public int getBarrierRegenPerSecond() { return barrierRegenPerSecond; }
    public void setBarrierRegenPerSecond(int regen) { this.barrierRegenPerSecond = Math.max(0, regen); markDirty(); }
    public void drainBarrierDurability(int amount) {
        if (amount <= 0) return;
        this.barrierDurability = Math.max(0, this.barrierDurability - amount);
        markDirty();
    }
    
    public void setBarrierDistances(int north, int south, int east, int west) {
        this.northDistance = north;
        this.southDistance = south;
        this.eastDistance = east;
        this.westDistance = west;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public void setCachedGlyphPositions(BlockPos north, BlockPos south, BlockPos east, BlockPos west) {
        this.northGlyphPos = north;
        this.southGlyphPos = south;
        this.eastGlyphPos = east;
        this.westGlyphPos = west;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    public int getNorthDistance() { return northDistance; }
    public int getSouthDistance() { return southDistance; }
    public int getEastDistance() { return eastDistance; }
    public int getWestDistance() { return westDistance; }
    
    // Ritual configuration methods
    public RitualConfiguration getRitualConfiguration() { return ritualConfiguration; }
    public void setRitualConfiguration(RitualConfiguration config) { 
        this.ritualConfiguration = config; 
        markDirty();
    }
    
    public BlockPos[] getDistanceGlyphs() {
        // Use cached glyph positions if available
        List<BlockPos> foundGlyphs = new ArrayList<>();
        if (northGlyphPos != null) foundGlyphs.add(northGlyphPos);
        if (southGlyphPos != null) foundGlyphs.add(southGlyphPos);
        if (eastGlyphPos != null) foundGlyphs.add(eastGlyphPos);
        if (westGlyphPos != null) foundGlyphs.add(westGlyphPos);
        
        return foundGlyphs.toArray(new BlockPos[0]);
    }

    private void captureAllowedPlayers() {
        if (world == null || world.isClient) {
            return;
        }
        this.allowedPlayerUuids.clear();
        double[] extents = computeExtents();
        double minX = extents[0], maxX = extents[1], minZ = extents[2], maxZ = extents[3];
        if (world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                double x = player.getX();
                double z = player.getZ();
                if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                    allowedPlayerUuids.add(player.getUuid());
                }
            }
        }
        System.out.println("BarrierCircle: Allowed players captured = " + allowedPlayerUuids.size());
    }

    public double[] computeExtents() {
        // Check if we have a ritual configuration
        if (ritualConfiguration != null) {
            if (ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.BARRIER) {
                // Barrier mode: Check barrier shape
                if (ritualConfiguration.getBarrierShape() == RitualConfiguration.BarrierShape.CIRCULAR) {
                    return computeCircularExtents();
                }
            } else if (ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
                // Effect mode: Always use circular area for effects
                return computeCircularExtents();
            }
        }
        
        // Default rectangular barrier
        int northRadius = Math.max(0, northDistance * 2);
        int southRadius = Math.max(0, southDistance * 2);
        int eastRadius = Math.max(0, eastDistance * 2);
        int westRadius = Math.max(0, westDistance * 2);
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        double minX = centerX - westRadius;
        double maxX = centerX + eastRadius;
        double minZ = centerZ - northRadius;
        double maxZ = centerZ + southRadius;
        return new double[]{minX, maxX, minZ, maxZ};
    }
    
    private double[] computeCircularExtents() {
        // For circular barriers, we need to compute the radius and center
        // Use the maximum distance from any direction as the radius
        int maxRadius = Math.max(Math.max(northDistance, southDistance), Math.max(eastDistance, westDistance));
        double radius = maxRadius * 2.0;
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        
        // Return circular bounds (we'll use these for distance calculations)
        return new double[]{centerX, centerZ, radius, 0}; // centerX, centerZ, radius, unused
    }

    private boolean isPlayerAllowed(ServerPlayerEntity player) {
        return allowedPlayerUuids.contains(player.getUuid());
    }

    public boolean isPlayerAllowedByUuid(UUID playerUuid) {
        return allowedPlayerUuids.contains(playerUuid);
    }

    public void addAllowedPlayer(UUID playerUuid) {
        allowedPlayerUuids.add(playerUuid);
        markDirty();
        System.out.println("BarrierCircle: Added player to allowlist. Total allowed: " + allowedPlayerUuids.size());
    }

    public void removeAllowedPlayer(UUID playerUuid) {
        allowedPlayerUuids.remove(playerUuid);
        markDirty();
        System.out.println("BarrierCircle: Removed player from allowlist. Total allowed: " + allowedPlayerUuids.size());
    }

    private void enforceBarrier() {
        if (world == null || world.isClient) return;
        if (!isBarrierFunctional()) return;
        
        // Check if we have a ritual configuration and use appropriate barrier logic
        if (ritualConfiguration != null && ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
            // EFFECT mode doesn't push players out
            return;
        }
        
        double[] extents = computeExtents();
        
        if (world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();
                
                boolean inside;
                double nx = 0, nz = 0, targetX = 0, targetZ = 0;
                
                if (ritualConfiguration != null && ritualConfiguration.getBarrierShape() == RitualConfiguration.BarrierShape.CIRCULAR) {
                    // Circular barrier logic
                    double centerX = extents[0];
                    double centerZ = extents[1];
                    double radius = extents[2];
                    double dx = x - centerX;
                    double dz = z - centerZ;
                    double distanceSq = dx * dx + dz * dz;
                    inside = distanceSq <= (radius * radius);
                    
                    if (inside && !isPlayerAllowed(player)) {
                        // Calculate outward normal from center
                        double distance = Math.sqrt(distanceSq);
                        if (distance > 0) {
                            nx = dx / distance;
                            nz = dz / distance;
                        } else {
                            nx = 1.0;
                            nz = 0.0;
                        }
                        
                        // Push to just outside the circle
                        targetX = centerX + nx * (radius + 0.01);
                        targetZ = centerZ + nz * (radius + 0.01);
                    } else {
                        continue; // Player not inside or allowed
                    }
                } else {
                    // Rectangular barrier logic
                    double minX = extents[0], maxX = extents[1], minZ = extents[2], maxZ = extents[3];
                    inside = x >= minX && x <= maxX && z >= minZ && z <= maxZ;
                    
                    if (inside && !isPlayerAllowed(player)) {
                        // Compute nearest boundary normal
                        double distToWest = x - minX;
                        double distToEast = maxX - x;
                        double distToNorth = z - minZ;
                        double distToSouth = maxZ - z;
                        double min = Math.min(Math.min(distToWest, distToEast), Math.min(distToNorth, distToSouth));
                        
                        if (min == distToWest) { nx = -1.0; targetX = minX - 0.01; }
                        else if (min == distToEast) { nx = 1.0; targetX = maxX + 0.01; }
                        else if (min == distToNorth) { nz = -1.0; targetZ = minZ - 0.01; }
                        else { nz = 1.0; targetZ = maxZ + 0.01; }
                        
                        if (min == distToWest || min == distToEast) {
                            nz = 0.0;
                            targetZ = z;
                        } else {
                            nx = 0.0;
                            targetX = x;
                        }
                    } else {
                        continue; // Player not inside or allowed
                    }
                }
                
                // Apply barrier physics (same for both shapes)
                var v = player.getVelocity();
                double inward = v.x * (-nx) + v.z * (-nz);
                if (inward > 0) {
                    // Remove inward component
                    double rx = inward * (-nx);
                    double rz = inward * (-nz);
                    v = v.add(-rx, 0, -rz);
                }
                
                // Apply outward push
                double push = 0.4;
                v = v.add(nx * push, 0, nz * push);
                player.setVelocity(v);
                player.velocityModified = true;

                // Clamp position to just outside boundary
                player.refreshPositionAfterTeleport(targetX, y, targetZ);

                // Optional feedback
                if (serverWorld.getTime() % 10 == 0) {
                    serverWorld.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 0.2f, 1.5f);
                }
            }
        }
    }
    
    public boolean containsPosition(Vec3d position) {
        double[] extents = computeExtents();
        
        // EFFECT mode is always circular; BARRIER is circular only if configured
        if (ritualConfiguration != null && (
                ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.EFFECT ||
                ritualConfiguration.getBarrierShape() == RitualConfiguration.BarrierShape.CIRCULAR)) {
            // Circular area check
            double centerX = extents[0];
            double centerZ = extents[1];
            double radius = extents[2];
            double dx = position.x - centerX;
            double dz = position.z - centerZ;
            return (dx * dx + dz * dz) <= (radius * radius);
        } else {
            // Rectangular bounds
            double minX = extents[0], maxX = extents[1], minZ = extents[2], maxZ = extents[3];
            return position.x >= minX && position.x <= maxX && position.z >= minZ && position.z <= maxZ;
        }
    }

    public void absorbExplosion(Vec3d explosionPos, float power) {
        if (!isRitualActive()) return;
        int damage = Math.max(1, Math.round(power * 50.0f));
        drainBarrierDurability(damage);
    }

    public static BarrierCircleBlockEntity findBarrierAt(World world, Vec3d pos) {
        for (BarrierCircleBlockEntity entity : LOADED_BARRIERS) {
            if (entity.world == world && entity.isBarrierFunctional() && entity.containsPosition(pos)) {
                return entity;
            }
        }
        return null;
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
        
        // Check if all required distance glyphs are still present (use cached positions)
        boolean hasNorthGlyph = northGlyphPos != null && world.getBlockState(northGlyphPos).isOf(ModBlocks.BARRIER_DISTANCE_GLYPH);
        boolean hasSouthGlyph = southGlyphPos != null && world.getBlockState(southGlyphPos).isOf(ModBlocks.BARRIER_DISTANCE_GLYPH);
        boolean hasEastGlyph = eastGlyphPos != null && world.getBlockState(eastGlyphPos).isOf(ModBlocks.BARRIER_DISTANCE_GLYPH);
        boolean hasWestGlyph = westGlyphPos != null && world.getBlockState(westGlyphPos).isOf(ModBlocks.BARRIER_DISTANCE_GLYPH);
        
        if (!hasNorth || !hasSouth || !hasEast || !hasWest) {
            System.out.println("BarrierCircle: Obelisk missing - ritual integrity broken! N:" + hasNorth + " S:" + hasSouth + " E:" + hasEast + " W:" + hasWest);
            return false;
        }
        
        if (!hasNorthGlyph || !hasSouthGlyph || !hasEastGlyph || !hasWestGlyph) {
            System.out.println("BarrierCircle: Distance glyph missing - ritual integrity broken! N:" + hasNorthGlyph + " S:" + hasSouthGlyph + " E:" + hasEastGlyph + " W:" + hasWestGlyph);
            return false;
        }
        
        return true;
    }
    
    private BlockPos findDistanceGlyph(BlockPos circlePos, Direction direction) {
        // Scan from 0 to 100 blocks in the specified direction
        for (int distance = 0; distance <= 100; distance++) {
            BlockPos checkPos = circlePos.offset(direction, distance);
            if (world.getBlockState(checkPos).isOf(ModBlocks.BARRIER_DISTANCE_GLYPH)) {
                return checkPos;
            }
        }
        return null; // No glyph found in this direction
    }
    
    /**
     * Deactivate the ritual and all obelisks
     */
    public void deactivateRitual() {
        if (world == null || world.isClient) return;
        
        System.out.println("BarrierCircle: Deactivating ritual due to integrity failure!");
        SkyRitualRenderer.removeActiveRitual(pos);

        // Reset ritual state
        this.ritualActive = false;
        this.ritualActivationStep = 0;
        this.step3StartTime = 0;
        this.allowedPlayerUuids.clear();
        
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
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        
        markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    
    private void deactivateObelisk(World world, BlockPos obeliskPos) {
        if (world.getBlockState(obeliskPos).isOf(ModBlocks.ACTIVE_OBELISK)) {
            // Get the texture variant from the active obelisk before converting
            int textureVariant = 0;
            BlockEntity activeObelisk = world.getBlockEntity(obeliskPos);
            if (activeObelisk instanceof ActiveObeliskBlockEntity activeObeliskEntity) {
                textureVariant = activeObeliskEntity.getTextureVariant();
            }
            
            world.setBlockState(obeliskPos, ModBlocks.OBELISK.getDefaultState());
            
            // Set the texture variant on the new obelisk block entity
            BlockEntity newObelisk = world.getBlockEntity(obeliskPos);
            if (newObelisk instanceof ObeliskBlockEntity obeliskEntity) {
                obeliskEntity.setTextureVariant(textureVariant);
            }
            
            // Spawn deactivation particles
            for (int i = 0; i < 10; i++) {
                double x = obeliskPos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                double y = obeliskPos.getY() + 1.0 + world.random.nextDouble() * 3.0;
                double z = obeliskPos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
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

        // Enforce barrier entry on every tick while active and functional
        if (entity.isBarrierFunctional()) {
            entity.enforceBarrier();
        }
        
        // Apply EFFECT mode pulses once per second
        if (entity.isRitualActive() && entity.ritualConfiguration != null &&
                entity.ritualConfiguration.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
            long now = world.getTime();
            if (now - entity.lastEffectTick >= 20) {
                entity.lastEffectTick = now;
                entity.pulseEffectArea();
            }
        }
        
        // Regenerate durability each second
        if (entity.isRitualActive() && entity.barrierDurability < entity.barrierMaxDurability) {
            long now = world.getTime();
            if (now - entity.lastRegenTick >= 20) {
                entity.lastRegenTick = now;
                entity.barrierDurability = Math.min(entity.barrierMaxDurability,
                        entity.barrierDurability + entity.barrierRegenPerSecond);
                entity.markDirty();
            }
        }
    }

    private void pulseEffectArea() {
        if (!(world instanceof ServerWorld serverWorld)) return;
        RitualConfiguration cfg = this.ritualConfiguration;
        if (cfg == null || cfg.getRitualType() != RitualConfiguration.RitualType.EFFECT) return;

        // Determine circular area
        int maxRadius = Math.max(Math.max(northDistance, southDistance), Math.max(eastDistance, westDistance)) * 2;
        double radius = Math.max(1, maxRadius);
        double centerX = pos.getX() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        // Search box around the circle area (full chunk height for maximum coverage)
        Box search = new Box(centerX - radius, 0, centerZ - radius,
                             centerX + radius, 256, centerZ + radius);

        // Map ritual effect type to status effect
        StatusEffect effect = switch (cfg.getEffectType()) {
            case POISON -> StatusEffects.POISON;
            case REGENERATION -> StatusEffects.REGENERATION;
            default -> null;
        };
        if (effect == null) return;

        // Amplifier and duration mapping
        int amplifier = Math.max(0, cfg.getStrength() - 1); // 1..3 -> 0..2
        int durationTicks = 20 * 5 * Math.max(1, cfg.getRegenRate()); // 5s per duration level

        // Gather targets within circle
        List<LivingEntity> targets = serverWorld.getEntitiesByClass(
                LivingEntity.class, search, e -> {
                    double dx = e.getX() - centerX;
                    double dz = e.getZ() - centerZ;
                    return (dx * dx + dz * dz) <= (radius * radius);
                }
        );

        if (!targets.isEmpty()) {
            StatusEffectInstance instance = new StatusEffectInstance(effect, durationTicks, amplifier, true, true, true);
            for (LivingEntity entity : targets) {
                // For negative effects (poison), only apply to denied players
                // For positive effects (regeneration), apply to allowed players
                if (cfg.getEffectType() == RitualConfiguration.EffectType.POISON) {
                    // Poison only affects players who aren't allowed to cross the barrier
                    if (entity instanceof ServerPlayerEntity player && isPlayerAllowed(player)) {
                        continue; // Skip allowed players for poison
                    }
                }
                else if (cfg.getEffectType() == RitualConfiguration.EffectType.REGENERATION) {
                    // Regeneration affects everyone, poison only affects denied players
                    if (entity instanceof ServerPlayerEntity player && !isPlayerAllowed(player)) {
                        continue; // Skip denied players for regeneration
                    }
                }
                entity.addStatusEffect(new StatusEffectInstance(instance));
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

    @Override
    public void markRemoved() {
        super.markRemoved();
        LOADED_BARRIERS.remove(this);
    }

} 