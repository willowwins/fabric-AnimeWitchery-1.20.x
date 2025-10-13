package net.willowins.animewitchery.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.ManaUtils;

import java.util.List;

/**
 * Handles spell casting logic for the spell system
 */
public class SpellCaster {
    
    private static final double BEAM_RANGE = 20.0;
    private static final float BASE_DAMAGE = 6.0f;
    
    // Mana costs for each spell type
    private static final int DAMAGE_SPELL_COST = 500;  // Fire, Earth, Wither, Light, Shadow
    private static final int UTILITY_SPELL_COST = 300; // Water Shield, Wind Gust
    private static final int HEALING_SPELL_COST = 800; // Healing Wave
    
    /**
     * Casts a spell from a player
     */
    public static boolean castSpell(PlayerEntity player, World world, String spellName) {
        if (world.isClient) return false;
        
        // Check and consume mana based on spell type
        int manaCost = getManaCost(spellName);
        if (!ManaUtils.consumeWithStorage(player, manaCost)) {
            player.sendMessage(Text.translatable("message.animewitchery.not_enough_mana", manaCost), true);
            return false;
        }
        
        return castSpellWithoutManaCost(player, world, spellName);
    }
    
    /**
     * Casts a spell without consuming mana (for spellbooks that pre-pay mana)
     */
    public static boolean castSpellWithoutManaCost(PlayerEntity player, World world, String spellName) {
        return castSpellWithoutManaCost(player, world, spellName, "direct");
    }
    
    /**
     * Casts a spell without consuming mana with specified targeting mode
     */
    public static boolean castSpellWithoutManaCost(PlayerEntity player, World world, String spellName, String targetingMode) {
        if (world.isClient) return false;
        
        switch (spellName) {
            case "Fire Blast" -> castFireBlast(player, world, targetingMode);
            case "Water Shield" -> castWaterShield(player, world, targetingMode);
            case "Earth Spike" -> castEarthSpike(player, world, targetingMode);
            case "Wind Gust" -> castWindGust(player, world, targetingMode);
            case "Healing Wave" -> castHealingWave(player, world, targetingMode);
            case "Wither Touch" -> castWitherTouch(player, world, targetingMode);
            case "Light Burst" -> castLightBurst(player, world, targetingMode);
            case "Shadow Bind" -> castShadowBind(player, world, targetingMode);
            default -> { return false; }
        }
        
        return true;
    }
    
    /**
     * Gets the mana cost for a spell
     */
    private static int getManaCost(String spellName) {
        return switch (spellName) {
            case "Fire Blast", "Earth Spike", "Wither Touch", "Light Burst", "Shadow Bind" -> DAMAGE_SPELL_COST;
            case "Water Shield", "Wind Gust" -> UTILITY_SPELL_COST;
            case "Healing Wave" -> HEALING_SPELL_COST;
            default -> 0;
        };
    }
    
    /**
     * Fire Blast - Shoots a beam of fire particles that damages and ignites entities
     */
    private static void castFireBlast(PlayerEntity player, World world) {
        castFireBlast(player, world, "direct");
    }
    
    /**
     * Fire Blast with targeting mode
     */
    private static void castFireBlast(PlayerEntity player, World world, String targetingMode) {
        ServerWorld serverWorld = (ServerWorld) world;
        
        switch (targetingMode) {
            case "direct" -> castFireBlastDirect(player, world, serverWorld);
            case "self" -> castFireBlastSelf(player, world, serverWorld);
            case "area" -> castFireBlastArea(player, world, serverWorld);
            case "surrounding" -> castFireBlastSurrounding(player, world, serverWorld);
            case "surrounding_target" -> castFireBlastSurroundingTarget(player, world, serverWorld);
            case "spell_wall" -> castFireBlastSpellWall(player, world, serverWorld);
            default -> castFireBlastDirect(player, world, serverWorld);
        }
    }
    
    /**
     * Direct fire blast (original behavior)
     */
    private static void castFireBlastDirect(PlayerEntity player, World world, ServerWorld serverWorld) {
        // Get player's look direction
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        // Raycast to find hit location
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        // Spawn particle beam
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.FLAME, ParticleTypes.LAVA);
        
        // Damage and ignite entities in the beam path
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(world.getDamageSources().magic(), BASE_DAMAGE);
                living.setOnFireFor(5);
            }
        }
    }
    
    /**
     * Area fire blast - targets multiple nearby entities
     */
    private static void castFireBlastArea(PlayerEntity player, World world, ServerWorld serverWorld) {
        Vec3d center = player.getPos().add(0, 1, 0);
        double radius = 8.0;
        
        // Find nearby entities
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, 
            new Box(center.add(-radius, -radius, -radius), center.add(radius, radius, radius)),
            entity -> entity != player && entity.isAlive()
        );
        
        // Limit to 5 targets maximum
        if (targets.size() > 5) {
            targets = targets.subList(0, 5);
        }
        
        for (LivingEntity target : targets) {
            Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
            
            // Spawn particle beam to each target
            spawnParticleBeam(serverWorld, center, targetPos, ParticleTypes.FLAME, ParticleTypes.LAVA);
            
            // Damage and ignite
            target.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.7f);
            target.setOnFireFor(3);
        }
        
        // If no targets, cast direct
        if (targets.isEmpty()) {
            castFireBlastDirect(player, world, serverWorld);
        }
    }
    
    /**
     * Surrounding fire blast - creates fire around the player
     */
    private static void castFireBlastSurrounding(PlayerEntity player, World world, ServerWorld serverWorld) {
        Vec3d center = player.getPos().add(0, 1, 0);
        double radius = 6.0;
        
        // Create 8 fire blasts in a circle around the player
        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * Math.PI * 2;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            Vec3d targetPos = new Vec3d(x, center.y, z);
            
            // Spawn particle beam outward
            spawnParticleBeam(serverWorld, center, targetPos, ParticleTypes.FLAME, ParticleTypes.LAVA);
            
            // Find and damage entities in the blast direction
            List<Entity> entities = getEntitiesInBeam(player, world, center, targetPos);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living && entity != player) {
                    living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.6f);
                    living.setOnFireFor(4);
                }
            }
        }
    }
    
    /**
     * Self-cast fire blast - casts directly on the player (useful for buffs/shields)
     */
    private static void castFireBlastSelf(PlayerEntity player, World world, ServerWorld serverWorld) {
        Vec3d pos = player.getPos().add(0, 1, 0);
        
        // Create fire effect around the player
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * 1.5;
            double z = pos.z + Math.sin(angle) * 1.5;
            double y = pos.y + (Math.random() - 0.5) * 2;
            
            serverWorld.spawnParticles(ParticleTypes.FLAME,
                x, y, z, 5, 0.1, 0.5, 0.1, 0.05);
        }
        
        // Apply damage to nearby enemies (not the caster)
        double radius = 3.0;
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, 
            new Box(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)),
            entity -> entity != player && entity.isAlive()
        );
        
        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.8f);
            target.setOnFireFor(5);
        }
    }
    
    /**
     * Surrounding target fire blast - casts around the targeted entity
     */
    private static void castFireBlastSurroundingTarget(PlayerEntity player, World world, ServerWorld serverWorld) {
        // Raycast to find target
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        // Find nearest entity along the ray
        List<Entity> entities = getEntitiesInBeam(player, world, start, end);
        LivingEntity target = null;
        double minDist = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                double dist = entity.squaredDistanceTo(player);
                if (dist < minDist) {
                    minDist = dist;
                    target = living;
                }
            }
        }
        
        // If no entity found, use hit position
        Vec3d targetPos = target != null ? target.getPos().add(0, 1, 0) : hitResult.getPos();
        
        // Create fire blasts in a circle around the target
        double radius = 3.0;
        for (int i = 0; i < 8; i++) {
            double angle = (i / 8.0) * Math.PI * 2;
            double x = targetPos.x + Math.cos(angle) * radius;
            double z = targetPos.z + Math.sin(angle) * radius;
            Vec3d blastPos = new Vec3d(x, targetPos.y, z);
            
            // Spawn particle beam inward toward target
            spawnParticleBeam(serverWorld, blastPos, targetPos, ParticleTypes.FLAME, ParticleTypes.LAVA);
            
            // Damage entities at blast position
            List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                new Box(blastPos.add(-1, -1, -1), blastPos.add(1, 1, 1)),
                entity -> entity.isAlive() && entity != player
            );
            
            for (LivingEntity t : targets) {
                t.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.7f);
                t.setOnFireFor(4);
            }
        }
    }
    
    /**
     * Spell wall fire blast - casts from both sides of the player towards the target
     */
    private static void castFireBlastSpellWall(PlayerEntity player, World world, ServerWorld serverWorld) {
        // Get player's look direction
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        // Raycast to find target
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d targetPos = hitResult.getPos();
        
        // Calculate perpendicular direction (left and right of player)
        Vec3d playerPos = player.getPos().add(0, 1, 0);
        Vec3d forward = targetPos.subtract(playerPos).normalize();
        Vec3d right = new Vec3d(-forward.z, 0, forward.x).normalize(); // Perpendicular vector
        
        // Cast 3 beams from each side (left and right)
        double sideDistance = 3.0;
        int beamsPerSide = 3;
        
        for (int i = 0; i < beamsPerSide; i++) {
            double offset = (i - (beamsPerSide - 1) / 2.0) * 1.5; // Spread along the perpendicular
            
            // Left side
            Vec3d leftStart = playerPos.add(right.multiply(-sideDistance)).add(forward.multiply(offset));
            spawnParticleBeam(serverWorld, leftStart, targetPos, ParticleTypes.FLAME, ParticleTypes.LAVA);
            
            List<Entity> leftEntities = getEntitiesInBeam(player, world, leftStart, targetPos);
            for (Entity entity : leftEntities) {
                if (entity instanceof LivingEntity living && entity != player) {
                    living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.5f);
                    living.setOnFireFor(3);
                }
            }
            
            // Right side
            Vec3d rightStart = playerPos.add(right.multiply(sideDistance)).add(forward.multiply(offset));
            spawnParticleBeam(serverWorld, rightStart, targetPos, ParticleTypes.FLAME, ParticleTypes.LAVA);
            
            List<Entity> rightEntities = getEntitiesInBeam(player, world, rightStart, targetPos);
            for (Entity entity : rightEntities) {
                if (entity instanceof LivingEntity living && entity != player) {
                    living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.5f);
                    living.setOnFireFor(3);
                }
            }
        }
    }
    
    /**
     * Water Shield - Creates a protective water barrier and grants resistance
     */
    private static void castWaterShield(PlayerEntity player, World world) {
        castWaterShield(player, world, "direct");
    }
    
    /**
     * Water Shield with targeting mode
     */
    private static void castWaterShield(PlayerEntity player, World world, String targetingMode) {
        ServerWorld serverWorld = (ServerWorld) world;
        Vec3d pos = player.getPos();
        
        switch (targetingMode) {
            case "direct" -> castWaterShieldDirect(player, world, serverWorld, pos);
            case "self" -> castWaterShieldDirect(player, world, serverWorld, pos); // Self-cast is same as direct for shields
            case "area" -> castWaterShieldArea(player, world, serverWorld, pos);
            case "surrounding" -> castWaterShieldSurrounding(player, world, serverWorld, pos);
            case "surrounding_target" -> castWaterShieldArea(player, world, serverWorld, pos); // Target area
            case "spell_wall" -> castWaterShieldArea(player, world, serverWorld, pos); // Spell wall uses area
            default -> castWaterShieldDirect(player, world, serverWorld, pos);
        }
    }
    
    /**
     * Direct water shield (original behavior)
     */
    private static void castWaterShieldDirect(PlayerEntity player, World world, ServerWorld serverWorld, Vec3d pos) {
        // Spawn initial water particles in a circle around the player
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * 2.5;
            double z = pos.z + Math.sin(angle) * 2.5;
            double y = pos.y + 1 + (Math.random() - 0.5) * 2;
            
            serverWorld.spawnParticles(ParticleTypes.FALLING_WATER,
                x, y, z, 8, 0.2, 0.8, 0.2, 0.3);
        }
        
        // Create a sustained water barrier effect
        createSustainedWaterBarrier(player, world, serverWorld, 300); // 15 seconds
        
        // Grant player resistance and fire resistance for 15 seconds
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 300, 0));
        
        // Extinguish player if on fire
        if (player.isOnFire()) {
            player.extinguish();
        }
    }
    
    /**
     * Creates a sustained water barrier effect around the player
     */
    private static void createSustainedWaterBarrier(PlayerEntity player, World world, ServerWorld serverWorld, int durationTicks) {
        // Schedule repeating particle effects for the duration
        for (int tick = 0; tick < durationTicks; tick += 10) { // Every 0.5 seconds
            ServerScheduler.schedule(serverWorld.getServer(), tick, () -> {
                if (!player.isAlive()) return;
                
                Vec3d pos = player.getPos();
                
                // Create flowing water particles around the player
                for (int i = 0; i < 20; i++) {
                    double angle = (i / 20.0) * Math.PI * 2;
                    double x = pos.x + Math.cos(angle) * 2.2;
                    double z = pos.z + Math.sin(angle) * 2.2;
                    double y = pos.y + 1 + (Math.random() - 0.5) * 1.5;
                    
                    serverWorld.spawnParticles(ParticleTypes.FALLING_WATER,
                        x, y, z, 3, 0.1, 0.4, 0.1, 0.1);
                }
                
                // Add some bubble particles for extra effect
                for (int i = 0; i < 8; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double x = pos.x + Math.cos(angle) * 2.0;
                    double z = pos.z + Math.sin(angle) * 2.0;
                    double y = pos.y + Math.random() * 2;
                    
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE,
                        x, y, z, 2, 0.1, 0.2, 0.1, 0.05);
                }
            });
        }
    }
    
    /**
     * Area water shield - affects nearby allies
     */
    private static void castWaterShieldArea(PlayerEntity player, World world, ServerWorld serverWorld, Vec3d pos) {
        double radius = 8.0;
        
        // Find nearby players/allies
        List<LivingEntity> allies = world.getEntitiesByClass(LivingEntity.class, 
            new Box(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)),
            entity -> entity.isAlive() && !entity.isOnFire()
        );
        
        // Apply shield to all nearby allies
        for (LivingEntity ally : allies) {
            Vec3d allyPos = ally.getPos();
            
            // Spawn water particles around ally
            for (int i = 0; i < 20; i++) {
                double angle = (i / 20.0) * Math.PI * 2;
                double x = allyPos.x + Math.cos(angle) * 2;
                double z = allyPos.z + Math.sin(angle) * 2;
                double y = allyPos.y + 1;
                
                serverWorld.spawnParticles(ParticleTypes.FALLING_WATER,
                    x, y, z, 3, 0.1, 0.3, 0.1, 0.1);
            }
            
            // Grant effects (reduced duration for allies)
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0));
            ally.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
            
            // Extinguish if on fire
            if (ally.isOnFire()) {
                ally.extinguish();
            }
        }
    }
    
    /**
     * Surrounding water shield - creates a larger barrier
     */
    private static void castWaterShieldSurrounding(PlayerEntity player, World world, ServerWorld serverWorld, Vec3d pos) {
        // Create larger water barrier with more particles
        for (int i = 0; i < 60; i++) {
            double angle = (i / 60.0) * Math.PI * 2;
            double x = pos.x + Math.cos(angle) * 4; // Larger radius
            double z = pos.z + Math.sin(angle) * 4;
            double y = pos.y + 1 + (Math.random() - 0.5) * 2; // Vary height
            
            serverWorld.spawnParticles(ParticleTypes.FALLING_WATER,
                x, y, z, 8, 0.2, 0.8, 0.2, 0.2);
        }
        
        // Enhanced effects for the caster
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 400, 1)); // Longer and stronger
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 400, 0));
        
        // Create sustained barrier for surrounding mode too
        createSustainedWaterBarrier(player, world, serverWorld, 400); // 20 seconds
        
        // Extinguish player if on fire
        if (player.isOnFire()) {
            player.extinguish();
        }
    }
    
    /**
     * Earth Spike - Damages and slows enemies
     */
    private static void castEarthSpike(PlayerEntity player, World world) {
        castEarthSpike(player, world, "direct");
    }
    
    /**
     * Earth Spike with targeting mode
     */
    private static void castEarthSpike(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT);
        
        // Damage and slow entities
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 1.2f);
                // Apply slowness for 3 seconds
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1));
            }
        }
    }
    
    /**
     * Wind Gust - Knockback beam
     */
    private static void castWindGust(PlayerEntity player, World world) {
        castWindGust(player, world, "direct");
    }
    
    /**
     * Wind Gust with targeting mode
     */
    private static void castWindGust(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.CLOUD, ParticleTypes.POOF);
        
        // Knockback entities
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                Vec3d knockback = direction.multiply(2.0);
                living.addVelocity(knockback.x, 0.5, knockback.z);
                living.velocityModified = true;
            }
        }
    }
    
    /**
     * Healing Wave - Heals entities and removes negative effects
     */
    private static void castHealingWave(PlayerEntity player, World world) {
        castHealingWave(player, world, "direct");
    }
    
    /**
     * Healing Wave with targeting mode
     */
    private static void castHealingWave(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.HEART, ParticleTypes.HAPPY_VILLAGER);
        
        // Heal entities and remove negative effects
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.heal(4.0f);
                // Remove negative effects
                living.removeStatusEffect(StatusEffects.POISON);
                living.removeStatusEffect(StatusEffects.WITHER);
                living.removeStatusEffect(StatusEffects.SLOWNESS);
                living.removeStatusEffect(StatusEffects.WEAKNESS);
                living.removeStatusEffect(StatusEffects.BLINDNESS);
                // Grant regeneration for 5 seconds
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0));
            }
        }
    }
    
    /**
     * Wither Touch - Damages and applies wither effect
     */
    private static void castWitherTouch(PlayerEntity player, World world) {
        castWitherTouch(player, world, "direct");
    }
    
    private static void castWitherTouch(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.SOUL, ParticleTypes.SOUL_FIRE_FLAME);
        
        // Damage and apply wither
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.8f);
                // Apply wither effect for 5 seconds
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
            }
        }
    }
    
    /**
     * Light Burst - Holy damage beam that deals extra damage to undead
     */
    private static void castLightBurst(PlayerEntity player, World world) {
        castLightBurst(player, world, "direct");
    }
    
    private static void castLightBurst(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.END_ROD, ParticleTypes.GLOW);
        
        // Damage entities with extra damage to undead
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                // Deal double damage to undead
                float damage = living.isUndead() ? BASE_DAMAGE * 2.0f : BASE_DAMAGE;
                living.damage(world.getDamageSources().magic(), damage);
                // Apply glowing effect for 5 seconds
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
            }
        }
    }
    
    /**
     * Shadow Bind - Damages and blinds/slows enemies
     */
    private static void castShadowBind(PlayerEntity player, World world) {
        castShadowBind(player, world, "direct");
    }
    
    private static void castShadowBind(PlayerEntity player, World world, String targetingMode) {
        // Fallback to direct for modes not specifically implemented
        if (!targetingMode.equals("direct") && !targetingMode.equals("self")) {
            targetingMode = "direct";
        }
        ServerWorld serverWorld = (ServerWorld) world;
        
        Vec3d start = player.getEyePos();
        Vec3d direction = player.getRotationVec(1.0f);
        Vec3d end = start.add(direction.multiply(BEAM_RANGE));
        
        HitResult hitResult = world.raycast(new RaycastContext(
            start, end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            player
        ));
        
        Vec3d actualEnd = hitResult.getPos();
        
        spawnParticleBeam(serverWorld, start, actualEnd, ParticleTypes.WITCH, ParticleTypes.PORTAL);
        
        // Damage and apply darkness effects
        List<Entity> entities = getEntitiesInBeam(player, world, start, actualEnd);
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(world.getDamageSources().magic(), BASE_DAMAGE * 0.7f);
                // Apply blindness and slowness for 4 seconds
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 80, 0));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 2));
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 0));
            }
        }
    }
    
    /**
     * Spawns a beam of particles from start to end
     */
    private static void spawnParticleBeam(ServerWorld world, Vec3d start, Vec3d end, 
                                         net.minecraft.particle.ParticleEffect particle1,
                                         net.minecraft.particle.ParticleEffect particle2) {
        Vec3d direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);
        
        // Spawn particles along the beam
        for (double d = 0; d < distance; d += 0.3) {
            Vec3d pos = start.add(direction.multiply(d));
            
            // Main beam particles
            world.spawnParticles(particle1, pos.x, pos.y, pos.z, 2, 0.05, 0.05, 0.05, 0.01);
            
            // Additional effect particles
            if (d % 1.0 < 0.3) {
                world.spawnParticles(particle2, pos.x, pos.y, pos.z, 1, 0.1, 0.1, 0.1, 0.05);
            }
        }
    }
    
    /**
     * Gets all entities in the beam path
     */
    private static List<Entity> getEntitiesInBeam(PlayerEntity caster, World world, Vec3d start, Vec3d end) {
        Vec3d direction = end.subtract(start).normalize();
        double distance = start.distanceTo(end);
        
        Box beamBox = new Box(start, end).expand(0.5);
        
        return world.getOtherEntities(caster, beamBox, entity -> {
            if (!(entity instanceof LivingEntity)) return false;
            
            // Check if entity is in the beam path
            Vec3d entityPos = entity.getPos().add(0, entity.getHeight() / 2, 0);
            Vec3d toEntity = entityPos.subtract(start);
            double projection = toEntity.dotProduct(direction);
            
            if (projection < 0 || projection > distance) return false;
            
            Vec3d closestPoint = start.add(direction.multiply(projection));
            double distanceToBeam = entityPos.distanceTo(closestPoint);
            
            return distanceToBeam < 1.0;
        });
    }
    
    /**
     * Damages all entities in the beam path
     */
    private static void damageEntitiesInBeam(PlayerEntity caster, World world, Vec3d start, Vec3d end, 
                                            float damage, String damageType) {
        List<Entity> entities = getEntitiesInBeam(caster, world, start, end);
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.damage(world.getDamageSources().magic(), damage);
            }
        }
    }
}
