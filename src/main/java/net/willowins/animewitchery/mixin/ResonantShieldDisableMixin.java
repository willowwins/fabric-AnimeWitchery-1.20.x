package net.willowins.animewitchery.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class ResonantShieldDisableMixin {

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    private void preventShieldDisable(boolean sprinting, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isUsingItem()) {
            ItemStack activeStack = player.getActiveItem();
            if (activeStack.getItem() == ModItems.RESONANT_SHIELD) {
                ci.cancel(); // Prevent shield disable
            }
        }
    }
}
