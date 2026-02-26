package net.willowins.animewitchery.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.util.ButcherContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class ButcherLootingEnchantmentMixin {

    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void getLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (ButcherContext.IS_PASSIVE_LOOTING.get()) {
            if (entity != null && entity.getMainHandStack().isOf(ModItems.BUTCHER_KNIFE)) {
                if (cir.getReturnValueI() < 3) {
                    cir.setReturnValue(3);
                }
            }
        }
    }
}
