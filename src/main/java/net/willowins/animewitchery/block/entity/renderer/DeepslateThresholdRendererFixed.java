package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.DeepslateThresholdBlockEntity;
import net.willowins.animewitchery.block.entity.model.DeepslateThresholdModel;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DeepslateThresholdRendererFixed implements BlockEntityRenderer<DeepslateThresholdBlockEntity> {
    private final GeoBlockRenderer<DeepslateThresholdBlockEntity> delegate;

    public DeepslateThresholdRendererFixed(BlockEntityRendererFactory.Context context) {
        this.delegate = new GeoBlockRenderer<>(new DeepslateThresholdModel());
    }

    // Default constructor if needed by registry (usually context is passed)
    public DeepslateThresholdRendererFixed() {
        this(null);
    }

    @Override
    public void render(DeepslateThresholdBlockEntity entity, float partialTick,
            net.minecraft.client.util.math.MatrixStack poseStack,
            net.minecraft.client.render.VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {

        poseStack.push();
        // GeoBlockRenderer handles rotation automatically via rotateBlock()
        // We just delegate.

        delegate.render(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.pop();
    }
}
