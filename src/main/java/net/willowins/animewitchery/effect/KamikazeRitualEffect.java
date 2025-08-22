package net.willowins.animewitchery.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.AnimeWitchery;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        if (entity instanceof PlayerEntity player && !entity.getWorld().isClient) {
            World world = entity.getWorld();
            BlockPos pos = entity.getBlockPos();
            
            // Check if this player is in a death sequence
            if (activeDeathSequences.containsKey(player.getUuid())) {
                DeathSequence sequence = activeDeathSequences.get(player.getUuid());
                sequence.update(world, player);
                return;
            }
            
            // Spawn ritual particles around the player
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
                
                // Cancel the death by setting health to 1 and making invulnerable
                player.setHealth(1.0f);
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
                
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cYour soul is bound! The ritual will complete in 10 seconds..."));
                
                return true; // Death was cancelled
            }
        }
        return false; // Death was not cancelled
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
            
            // Keep health at 1 and maintain invulnerability
            player.setHealth(1.0f);
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
            if (ticks == 40) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c9 seconds remaining..."));
            if (ticks == 60) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c8 seconds remaining..."));
            if (ticks == 80) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c7 seconds remaining..."));
            if (ticks == 100) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c6 seconds remaining..."));
            if (ticks == 120) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c5 seconds remaining..."));
            if (ticks == 140) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c4 seconds remaining..."));
            if (ticks == 160) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c3 seconds remaining..."));
            if (ticks == 180) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c2 seconds remaining..."));
            if (ticks == 190) player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §c1 second remaining..."));
            
            // Final countdown
            if (ticks == 195) {
                player.sendMessage(Text.literal("§4§l[FORBIDDEN MAGIC] §cRITUAL COMPLETE!"));
                world.playSound(null, deathPos, SoundEvents.ENTITY_GENERIC_EXPLODE, 
                    player.getSoundCategory(), 2.0f, 0.5f);
            }
            
            // Execute the ritual
            if (ticks >= TOTAL_TICKS) {
                executeRitual(world, player);
                activeDeathSequences.remove(player.getUuid());
                isRitualActive = false; // Reset flag after ritual completes
            }
        }
        
        private void executeRitual(World world, PlayerEntity player) {
            // Create massive explosion (power 20, TNT is 4)
            world.createExplosion(
                player, // Source entity
                deathPos.getX() + 0.5, deathPos.getY() + 0.5, deathPos.getZ() + 0.5, // Position
                20.0f, // Power (5x TNT)
                false, // Create fire
                World.ExplosionSourceType.TNT // Explosion type
            );
            
            // Add magical explosion particles
            for (int i = 0; i < 150; i++) {
                double angle = world.random.nextDouble() * Math.PI * 2.0;
                double radius = world.random.nextDouble() * 25.0;
                double x = deathPos.getX() + 0.5 + Math.cos(angle) * radius;
                double z = deathPos.getZ() + 0.5 + Math.sin(angle) * radius;
                
                world.addParticle(ParticleTypes.END_ROD, x, deathPos.getY() + 0.5, z, 0, 0.5, 0);
                world.addParticle(ParticleTypes.FIREWORK, x, deathPos.getY() + 0.5, z, 0, 0.5, 0);
                world.addParticle(ParticleTypes.EXPLOSION, x, deathPos.getY() + 0.5, z, 0, 0.5, 0);
            }
            
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
    }
}
