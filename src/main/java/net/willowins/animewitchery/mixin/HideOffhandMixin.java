package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import net.willowins.animewitchery.item.custom.ResonantShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HideOffhandMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void hideOffhandInFirstPerson(net.minecraft.entity.LivingEntity entity, ItemStack stack,
            net.minecraft.client.render.model.json.ModelTransformationMode renderMode,
            boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayerEntity player) {
            ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
            ItemStack offHandStack = player.getStackInHand(Hand.OFF_HAND);

            // If the main hand is holding the resonant greatsword and we're trying to
            // render the offhand,
            // hide it unless it's the resonant shield.
            if (mainHandStack.getItem() instanceof ResonantGreatSwordItem && stack == offHandStack) {
                if (!(offHandStack.getItem() instanceof ResonantShieldItem)) {
                    ci.cancel();
                }
            }
        }
    }
}
