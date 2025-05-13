package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class UndeadMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void damage(Entity target, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player && target instanceof LivingEntity livingEntity && livingEntity.isUndead() && player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                if (player.getMainHandStack().isOf(ModItems.NEEDLE) ) {
                    target.damage(target.getDamageSources().playerAttack(player), 5 * 2.5f);
                }

        }
    }
}
