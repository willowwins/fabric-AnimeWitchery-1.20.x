package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.custom.RailgunItem;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.willowins.animewitchery.client.render.feature.ResonantEyeFeatureRenderer;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    // Removed @Shadow to avoid lookup failures

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void reignNoLonger$getArmPoseDR(AbstractClientPlayerEntity player, Hand hand,
            CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Railgun arm pose when using
        if (itemStack.getItem() instanceof RailgunItem) {
            if (player.isUsingItem()) {
                cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
            }
        }

        // Resonant Greatsword & Oathbreaker arm pose
        if (itemStack.getItem() instanceof ResonantGreatSwordItem
                || itemStack.getItem() instanceof net.willowins.animewitchery.item.custom.OathbreakerItem) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void animewitchery$addEyeGlowLayer(net.minecraft.client.render.entity.EntityRendererFactory.Context ctx,
            boolean slim, CallbackInfo ci) {

        // Use Accessor to invoke addFeature on 'this'
        // We cast 'this' to the Accessor interface.
        // The JVM allows this cast because the target class
        // (PlayerEntityRenderer/LivingEntityRenderer)
        // will implement the interface via Mixin.

        ((LivingEntityRendererAccessor<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>) (Object) this)
                .callAddFeature(new ResonantEyeFeatureRenderer(
                        (net.minecraft.client.render.entity.feature.FeatureRendererContext) (Object) this));
    }
}
