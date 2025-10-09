package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;
import net.willowins.animewitchery.block.entity.model.GrandShulkerBoxModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class GrandShulkerBoxRenderer extends GeoBlockRenderer<GrandShulkerBoxBlockEntity> {
    
    public GrandShulkerBoxRenderer(BlockEntityRendererFactory.Context context) {
        super(new GrandShulkerBoxModel());
    }
    
    @Override
    public void actuallyRender(MatrixStack poseStack, GrandShulkerBoxBlockEntity animatable, BakedGeoModel model, 
                              RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, 
                              boolean isReRender, float partialTick, int packedLight, int packedOverlay, 
                              float red, float green, float blue, float alpha) {
        
        // Rotate the model 90 degrees down (around negative X-axis)
        // and center it properly within the block outline
        poseStack.push();
        poseStack.multiply(net.minecraft.util.math.RotationAxis.NEGATIVE_X.rotationDegrees(90));
        poseStack.translate(0, -0.5, -0.5); // Move forward half a block on Z-axis

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, 
                           isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        
        poseStack.pop();
    }
}
