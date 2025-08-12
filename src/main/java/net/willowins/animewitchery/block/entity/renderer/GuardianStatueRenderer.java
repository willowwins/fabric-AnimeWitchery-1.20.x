package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.block.entity.GuardianStatueBlockEntity;
import net.willowins.animewitchery.block.entity.model.GuardianStatueModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GuardianStatueRenderer extends GeoBlockRenderer<GuardianStatueBlockEntity> {
    
    public GuardianStatueRenderer(BlockEntityRendererFactory.Context context) {
        super(new GuardianStatueModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, GuardianStatueBlockEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        BlockState state = animatable.getCachedState();
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        poseStack.push();

        // Apply rotation based on facing direction
        float rotation = 0;
        switch (facing) {
            case NORTH -> rotation = 0;
            case EAST -> rotation = 90;
            case SOUTH -> rotation = 180;
            case WEST -> rotation = 270;
        }

        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }
}
