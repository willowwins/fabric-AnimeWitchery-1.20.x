package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.events.CapturingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class EntityDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onEntityDeath(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check if the entity was killed by a player
        if (damageSource.getAttacker() instanceof PlayerEntity player) {
            CapturingHandler.onEntityDeath(entity, player);
        }
    }

    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    private void preventScytheAndOwnerDrops(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        // Prevent drops if killed by Soul Scythe
        if (source.getAttacker() instanceof PlayerEntity player) {
            if (player.getMainHandStack().getItem() instanceof net.willowins.animewitchery.item.custom.SoulScytheItem) {
                ci.cancel();
                return;
            }
        }

        // Prevent drops if entity has an owner (Summoned)
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof net.willowins.animewitchery.entity.ISummonedEntity summoned
                && summoned.getSummonerUuid() != null) {
            ci.cancel();
        }
    }
}
