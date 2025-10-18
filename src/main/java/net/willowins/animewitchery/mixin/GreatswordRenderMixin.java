package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class GreatswordRenderMixin {
    
    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void rotateGreatswordWhenBlockingFirstPerson(AbstractClientPlayerEntity player, float tickDelta, float pitch, 
                                               net.minecraft.util.Hand hand, float swingProgress, ItemStack item,
                                               float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                               int light, CallbackInfo ci) {
        if (item.getItem() instanceof ResonantGreatSwordItem && player.isUsingItem()) {
            // Rotate 45 degrees when blocking (first person)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));
        }
    }
    
    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    private void rotateGreatswordWhenBlockingThirdPerson(LivingEntity entity, ItemStack stack, 
                                                          ModelTransformationMode renderMode, boolean leftHanded,
                                                          MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                                          int light, CallbackInfo ci) {
        if (stack.getItem() instanceof ResonantGreatSwordItem && entity.isUsingItem() && 
            (renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || 
             renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND)) {
            // Rotate 45 degrees when blocking (third person)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));
        }
    }
}

