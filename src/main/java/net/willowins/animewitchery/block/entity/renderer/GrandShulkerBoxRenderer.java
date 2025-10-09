package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
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
        
        // Apply rotation based on the block's facing direction
        var facing = animatable.getCachedState().get(net.minecraft.state.property.Properties.FACING);
        
        switch (facing) {
            case DOWN -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                poseStack.translate(-0.5, -0.5, -0.5);
            }
            case UP -> {
                // Default orientation, no rotation needed
            }
            case NORTH -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
                poseStack.translate(-0.5, -0.5, -0.5);
            }
            case SOUTH -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
                poseStack.translate(-0.5, -0.5, -0.5);
            }
            case WEST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
                poseStack.translate(-0.5, -0.5, -0.5);
            }
            case EAST -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90));
                poseStack.translate(-0.5, -0.5, -0.5);
            }
        }
        
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, 
                           isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
