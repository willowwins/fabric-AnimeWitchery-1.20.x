package net.willowins.animewitchery.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.willowins.animewitchery.effect.KamikazeRitualEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerDeathMixin {
    
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onPlayerDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Check if this damage would kill the player and they have the ritual effect
        // Also check if a ritual is already active to prevent infinite loops
        if (player.hasStatusEffect(net.willowins.animewitchery.effect.ModEffect.KAMIKAZE_RITUAL) 
            && (player.getHealth() - amount) <= 0.0f
            && !net.willowins.animewitchery.effect.KamikazeRitualEffect.isRitualActive) {
            
            // Try to cancel the death and start the ritual sequence
            boolean deathCancelled = KamikazeRitualEffect.onPlayerDeath(player, source);
            if (deathCancelled) {
                // Cancel the damage entirely and let the ritual handle it
                cir.setReturnValue(false);
            }
        }
    }
    
    // Prevent death processing entirely during ritual
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onPlayerDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // If a ritual is active, cancel death processing
        if (net.willowins.animewitchery.effect.KamikazeRitualEffect.isRitualActive) {
            ci.cancel();
        }
    }
    
    // Override the isDead check to keep player "alive" during ritual
    @Inject(method = "isDead", at = @At("HEAD"), cancellable = true)
    private void onIsDead(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // If a ritual is active, player is not considered dead
        if (net.willowins.animewitchery.effect.KamikazeRitualEffect.isRitualActive) {
            cir.setReturnValue(false);
        }
    }
}
