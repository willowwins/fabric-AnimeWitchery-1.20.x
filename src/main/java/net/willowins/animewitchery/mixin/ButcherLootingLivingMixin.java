package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.willowins.animewitchery.util.ButcherContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class ButcherLootingLivingMixin {

    @Inject(method = "dropLoot", at = @At("HEAD"))
    private void onDropLootHead(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if ((Object) this instanceof PassiveEntity) {
            ButcherContext.IS_PASSIVE_LOOTING.set(true);
        }
    }

    @Inject(method = "dropLoot", at = @At("RETURN"))
    private void onDropLootReturn(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        ButcherContext.IS_PASSIVE_LOOTING.set(false);
    }
}
