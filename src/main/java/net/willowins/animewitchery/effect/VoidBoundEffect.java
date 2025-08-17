package net.willowins.animewitchery.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.util.VoidPhaseUtil;
// Removed unused shader-related imports; Lodestone PostProcessor handles rendering

public class VoidBoundEffect extends StatusEffect {

    private static final float RISE_TIME_T   = 40f;   // 0 → 1   (~2.0s)
    private static final float HOLD_MAX_T    = 200f;  // hold 1  (~10s)
    private static final float FALL_TIME_T   = 40f;   // 1 → 0   (~2.0s)
    private static final float HOLD_MIN_T    = 40f;   // hold 0  (~2.0s)

    public VoidBoundEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x000000); // Pure black color for void theme
    }
    
    // Shader is controlled by Lodestone PostProcessor; no direct JSON shader loading here
    
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Apply effect every tick for smooth phasing
        return true;
    }
    
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player) {
            // Calculate phasing cycle (faster with higher amplifier)
            float voidPhase = VoidPhaseUtil.computePhase(player.age, amplifier); // 0..1


            // Apply invisibility during void phase
            if (voidPhase > 0.99f) {
                // Void phase - invisible and invulnerable, but fully mobile
                player.setInvisible(true);
                player.setInvulnerable(true);
                
                // Add void particles around the player
                if (player.getWorld() != null && player.getWorld().isClient) {
                    spawnVoidParticles(player);
                    // Turn ON the void shader
                    //net.willowins.animewitchery.client.shader.VoidBoundPostProcessor.INSTANCE.setActive(true);
                }
            } else {
                // Normal phase - visible and vulnerable
                player.setInvisible(false);
                player.setInvulnerable(false);
                
                // Turn OFF the void shader
                if (player.getWorld() != null && player.getWorld().isClient) {
                    //net.willowins.animewitchery.client.shader.VoidBoundPostProcessor.INSTANCE.setActive(false);
                }
            }
        }
    }
    
    @Override
    public void onRemoved(LivingEntity entity, net.minecraft.entity.attribute.AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        
        // Ensure PostProcessor is turned off when effect completely ends
        if (entity instanceof PlayerEntity player && player.getWorld() != null && player.getWorld().isClient) {
            //net.willowins.animewitchery.client.shader.VoidBoundPostProcessor.INSTANCE.setActive(false);
        }
    }
    
    private void spawnVoidParticles(PlayerEntity player) {
        // This would integrate with your Lodestone particle system
        // For now, we'll use vanilla particles as a placeholder
        if (player.getWorld() != null && player.getWorld().isClient) {
            for (int i = 0; i < 3; i++) {
                double offsetX = (player.getWorld().getRandom().nextDouble() - 0.5) * 0.8;
                double offsetY = player.getWorld().getRandom().nextDouble() * 0.6;
                double offsetZ = (player.getWorld().getRandom().nextDouble() - 0.5) * 0.8;
                
                // Use end rod particles for void effect (you can replace with Lodestone later)
                player.getWorld().addParticle(
                    net.minecraft.particle.ParticleTypes.END_ROD,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    0.0, 0.02, 0.0
                );
            }
        }
    }
    
    @Override
    public String getTranslationKey() {
        return "effect.animewitchery.void_bound";
    }
    
    // Prevent interaction with blocks during void phase
    public static boolean canInteractWithWorld(PlayerEntity player) {
        if (player.hasStatusEffect(ModEffect.VOID_BOUND)) {
            var effect = player.getStatusEffect(ModEffect.VOID_BOUND);
            float voidPhase = VoidPhaseUtil.computePhase(player.age, effect.getAmplifier());
            return voidPhase <= 0.99f; // Can only interact when NOT in void phase
        }
        return true; // Can always interact if no void bound effect
    }
    

    
    private void applyVoidShader(boolean active, float voidPhase) {
        // The PostProcessor is now automatically handled by Lodestone
        // It will apply the shader when the effect is active
        // The shader intensity is controlled by the VoidPhase uniform
        if (active) {
            // Store the void phase for the shader to use
            // This could be stored in a global variable or passed to the PostProcessor
            System.out.println("Void shader active with phase: " + voidPhase);
        } else {
            System.out.println("Void shader inactive");
        }
    }
}
