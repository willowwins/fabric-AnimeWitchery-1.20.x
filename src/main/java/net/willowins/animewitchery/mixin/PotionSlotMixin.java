package net.willowins.animewitchery.mixin;

import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public class PotionSlotMixin {

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void allowPotionFlaskInGui(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(ModItems.POTION_FLASK) || stack.isOf(ModItems.EMPTY_FLASK)) {
            cir.setReturnValue(true);
        }
    }
}
