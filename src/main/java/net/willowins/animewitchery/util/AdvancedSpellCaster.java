package net.willowins.animewitchery.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;

/**
 * Advanced spell caster that handles individual spell positioning, targeting, delays, and multiplicities
 */
public class AdvancedSpellCaster {
    
    /**
     * Cast an advanced spell configuration
     */
    public static void castAdvancedConfiguration(PlayerEntity caster, World world, AdvancedSpellConfiguration config) {
        if (world.isClient) return;
        
        ServerWorld serverWorld = (ServerWorld) world;
        
        // Get target information once at cast time
        TargetInfo targetInfo = getTargetInfo(caster, world);
        
        // Cast each spell with its individual configuration
        for (AdvancedSpellConfiguration.SpellEntry spellEntry : config.getSpells()) {
            int totalDelayTicks = (config.getGlobalDelay() + spellEntry.getDelay()) / 50; // Convert ms to ticks
            
            if (totalDelayTicks > 0) {
                // Schedule delayed casting using server scheduler
                net.willowins.animewitchery.util.ServerScheduler.schedule(
                    serverWorld.getServer(), 
                    totalDelayTicks, 
                    () -> castSpellEntry(caster, world, spellEntry, targetInfo)
                );
            } else {
                // Cast immediately
                castSpellEntry(caster, world, spellEntry, targetInfo);
            }
        }
    }
    
    /**
     * Cast a single spell entry with its configuration
     */
    private static void castSpellEntry(PlayerEntity caster, World world, AdvancedSpellConfiguration.SpellEntry spellEntry, TargetInfo targetInfo) {
        ServerWorld serverWorld = (ServerWorld) world;
        
        // Cast multiple instances if multiplicity > 1
        for (int i = 0; i < spellEntry.getMultiplicity(); i++) {
            final int instanceIndex = i;
            
            // Add progressive delay between multiple instances (2 ticks = 100ms per instance)
            int instanceDelayTicks = i * 2;
            
            if (instanceDelayTicks > 0) {
                // Schedule with proper server scheduler
                net.willowins.animewitchery.util.ServerScheduler.schedule(
                    serverWorld.getServer(),
                    instanceDelayTicks,
                    () -> castSingleSpellInstance(caster, world, spellEntry, targetInfo, instanceIndex)
                );
            } else {
                // Cast immediately
                castSingleSpellInstance(caster, world, spellEntry, targetInfo, instanceIndex);
            }
        }
    }
    
    /**
     * Cast a single spell instance at the calculated position
     */
    private static void castSingleSpellInstance(PlayerEntity caster, World world, AdvancedSpellConfiguration.SpellEntry spellEntry, TargetInfo targetInfo, int instanceIndex) {
        // Simplified casting - just cast from player's current position
        // This avoids issues with teleporting the player
        net.willowins.animewitchery.util.SpellCaster.castSpellWithoutManaCost(caster, world, spellEntry.getSpellName(), getTargetingMode(spellEntry.getTargeting()));
    }
    
    /**
     * Calculate the position where the spell should be cast
     */
    private static Vec3d calculateCastPosition(PlayerEntity caster, World world, AdvancedSpellConfiguration.SpellEntry spellEntry, TargetInfo targetInfo, int instanceIndex) {
        Vec3d basePosition = caster.getPos().add(0, caster.getEyeHeight(caster.getPose()), 0);
        Vec3d targetPosition = targetInfo.position;
        
        Vec3d position = switch (spellEntry.getPosition()) {
            case SELF -> basePosition;
            case FRONT -> basePosition.add(caster.getRotationVector().multiply(2.0));
            case BACK -> basePosition.add(caster.getRotationVector().multiply(-2.0));
            case LEFT -> basePosition.add(caster.getRotationVector().rotateY((float) Math.PI / 2).multiply(2.0));
            case RIGHT -> basePosition.add(caster.getRotationVector().rotateY(-(float) Math.PI / 2).multiply(2.0));
            case UP -> basePosition.add(0, 3, 0);
            case DOWN -> basePosition.add(0, -2, 0);
            case TARGET -> targetPosition;
            case TARGET_FRONT -> targetPosition.add(targetInfo.direction.multiply(2.0));
            case TARGET_BACK -> targetPosition.add(targetInfo.direction.multiply(-2.0));
            case TARGET_LEFT -> targetPosition.add(targetInfo.direction.rotateY((float) Math.PI / 2).multiply(2.0));
            case TARGET_RIGHT -> targetPosition.add(targetInfo.direction.rotateY(-(float) Math.PI / 2).multiply(2.0));
            case TARGET_UP -> targetPosition.add(0, 3, 0);
            case TARGET_DOWN -> targetPosition.add(0, -2, 0);
            case CUSTOM -> basePosition.add(spellEntry.getOffset());
        };
        
        // Add instance-based offset for multiplicity with circular spread
        if (spellEntry.getMultiplicity() > 1) {
            // Arrange in a circle around the firing point
            float angle = (float) (2 * Math.PI * instanceIndex / spellEntry.getMultiplicity());
            
            // Base radius increases with more spells to prevent overlap
            float baseRadius = 1.0f + (spellEntry.getMultiplicity() / 5.0f);
            
            // Add slight random variation to the radius for more natural spread
            float radiusVariation = (float) (Math.random() * 0.3 - 0.15); // ±0.15
            float radius = baseRadius + radiusVariation;
            
            // Calculate horizontal offset in a circle
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            
            // Add slight vertical variation for more interesting patterns
            double offsetY = (Math.random() * 0.4 - 0.2); // ±0.2 blocks vertically
            
            Vec3d instanceOffset = new Vec3d(offsetX, offsetY, offsetZ);
            position = position.add(instanceOffset);
        }
        
        return position;
    }
    
    /**
     * Calculate the direction the spell should face
     */
    private static Vec3d calculateCastDirection(PlayerEntity caster, World world, AdvancedSpellConfiguration.SpellEntry spellEntry, TargetInfo targetInfo, Vec3d castPosition) {
        return switch (spellEntry.getTargeting()) {
            case CASTER -> caster.getPos().subtract(castPosition).normalize();
            case TARGET_ENTITY, TARGET_BLOCK, AUTO -> targetInfo.position.subtract(castPosition).normalize();
            case AREA -> Vec3d.ZERO; // No specific direction for area effects
            case PROJECTILE -> caster.getRotationVector();
        };
    }
    
    /**
     * Convert advanced targeting to the existing targeting system
     */
    private static String getTargetingMode(AdvancedSpellConfiguration.SpellTargeting targeting) {
        return switch (targeting) {
            case CASTER -> "self";
            case TARGET_ENTITY -> "surrounding_target";
            case TARGET_BLOCK -> "direct";
            case AUTO -> "area";
            case AREA -> "area";
            case PROJECTILE -> "direct";
        };
    }
    
    /**
     * Get target information from the caster's aim
     */
    private static TargetInfo getTargetInfo(PlayerEntity caster, World world) {
        // Perform raycast to find what the player is looking at
        double reachDistance = 50.0;
        Vec3d start = caster.getEyePos();
        Vec3d end = start.add(caster.getRotationVector().multiply(reachDistance));
        
        HitResult hitResult = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster));
        
        Vec3d targetPosition;
        Vec3d targetDirection = caster.getRotationVector();
        
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            targetPosition = entityHit.getEntity().getPos();
            targetDirection = entityHit.getEntity().getPos().subtract(caster.getEyePos()).normalize();
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            targetPosition = Vec3d.ofCenter(blockHit.getBlockPos());
            targetDirection = targetPosition.subtract(caster.getEyePos()).normalize();
        } else {
            targetPosition = end;
        }
        
        return new TargetInfo(targetPosition, targetDirection, hitResult.getType());
    }
    
    /**
     * Target information container
     */
    private static class TargetInfo {
        final Vec3d position;
        final Vec3d direction;
        final HitResult.Type type;
        
        TargetInfo(Vec3d position, Vec3d direction, HitResult.Type type) {
            this.position = position;
            this.direction = direction;
            this.type = type;
        }
    }
}
