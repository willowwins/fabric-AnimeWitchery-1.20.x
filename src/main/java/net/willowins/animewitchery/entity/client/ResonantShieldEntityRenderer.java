package net.willowins.animewitchery.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.entity.custom.ResonantShieldEntity;
import net.willowins.animewitchery.item.ModItems;

@Environment(EnvType.CLIENT)
public class ResonantShieldEntityRenderer extends EntityRenderer<ResonantShieldEntity> {
    private final ItemRenderer itemRenderer;

    public ResonantShieldEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ResonantShieldEntity entity, float yaw, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Translate up to center the item in the entity's hitbox (pivot is at feet)
        matrices.translate(0, 0.5, 0);

        // Scale based on entity size/damage absorbed
        // entity.getDimensions(EntityPose.STANDING).width is already scaled by
        // getDimensions in the entity class
        // But we can also access the data tracker if needed.
        // Let's rely on width ratio. Base width is 1.0.
        float scale = entity.getWidth();

        matrices.scale(scale, scale, scale);

        // Rotate to face correct direction (billboard or fixed?)
        // The entity yaw/pitch are set to owner's yaw/pitch in tick()
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));

        // Render the shield item model (which uses the entity texture)
        this.itemRenderer.renderItem(ModItems.RESONANT_SHIELD_ENTITY_MODEL.getDefaultStack(),
                ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers,
                entity.getWorld(), entity.getId());

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(ResonantShieldEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
    }
}
