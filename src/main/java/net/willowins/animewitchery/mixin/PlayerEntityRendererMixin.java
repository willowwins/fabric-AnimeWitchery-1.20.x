package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.custom.RailgunItem;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void reignNoLonger$getArmPoseDR(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        
        // Railgun arm pose when using
        if (itemStack.getItem() instanceof RailgunItem) {
            if (player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
            }
        }
        
        // Resonant Greatsword arm pose - always use crossbow charge (two-handed) pose
        if (itemStack.getItem() instanceof ResonantGreatSwordItem) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
        }
    }

}
