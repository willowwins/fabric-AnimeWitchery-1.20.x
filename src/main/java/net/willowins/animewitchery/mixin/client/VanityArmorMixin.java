package net.willowins.animewitchery.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.willowins.animewitchery.AnimeWitcheryClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class VanityArmorMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> {

    @Inject(method = "render", at = @At("HEAD"))
    private void validArmorRenderHead(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch, CallbackInfo ci) {
        if (entity instanceof net.minecraft.entity.player.PlayerEntity) {
            net.willowins.animewitchery.client.CosmeticArmorState.isRendering = true;
            // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("Vanity: Flag ON
            // (Render Loop)");
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void validArmorRenderReturn(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch, CallbackInfo ci) {
        net.willowins.animewitchery.client.CosmeticArmorState.isRendering = false;
        // net.willowins.animewitchery.AnimeWitchery.LOGGER.info("Vanity: Flag OFF
        // (Render Loop)");
    }
}
