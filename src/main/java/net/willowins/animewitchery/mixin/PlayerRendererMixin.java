package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.util.VoidPhaseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(
            method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRender(AbstractClientPlayerEntity entity, float f, float g,
                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                          int light, CallbackInfo ci) {
        // AbstractClientPlayerEntity extends Entity -> has public int age
        float voidPhase = VoidPhaseUtil.computePhase(entity.age, 0);
        if (voidPhase > 0.99f) {
            ci.cancel(); // cancel all rendering for this player
        }
    }
}