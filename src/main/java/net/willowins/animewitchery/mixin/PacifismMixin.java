package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.effect.ModEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class PacifismMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void handlePacifismDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Allow Thorns damage to pass through
        if (source.isOf(net.minecraft.entity.damage.DamageTypes.THORNS)) {
            return;
        }

        Entity attacker = source.getAttacker();

        // Check if damage is coming from a player
        if (attacker instanceof PlayerEntity player) {
            // Check if player has Pacifism effect
            if (player.hasStatusEffect(ModEffect.PACIFISM)) {
                LivingEntity target = (LivingEntity) (Object) this;

                // Check if target is Player or Passive Entity (animals, villagers)
                if (target instanceof PlayerEntity || target instanceof PassiveEntity) {
                    // Cancel damage completely
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
