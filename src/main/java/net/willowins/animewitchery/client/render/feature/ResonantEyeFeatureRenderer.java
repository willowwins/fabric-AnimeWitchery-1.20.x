package net.willowins.animewitchery.client.render.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ResonantEyeFeatureRenderer
        extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier TEXTURE = new Identifier("animewitchery", "textures/entity/resonant_eye.png");

    public ResonantEyeFeatureRenderer(
            FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            AbstractClientPlayerEntity player, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {

        // UUID Check: fb4a9150-fc53-4f62-a0ec-a03d8e57c77d
        if (!player.getUuid().toString().equals("fb4a9150-fc53-4f62-a0ec-a03d8e57c77d")) {
            return;
        }

        if (player.isInvisible())
            return;

        // Render the model using the glowing texture
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE));

        // We only want to render the Head part of the model with this texture
        this.getContextModel().head.render(matrices, vertexConsumer, light, 15728880, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
