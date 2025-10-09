package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.block.entity.PlateBlockEntity;
import static net.willowins.animewitchery.block.custom.PlateBlock.HAS_ITEM;

public class PlateRenderer implements BlockEntityRenderer<PlateBlockEntity> {
    private final ItemRenderer itemRenderer;

    public PlateRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PlateBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.getWorld() == null) {
            return;
        }

        BlockState state = entity.getCachedState();
        if (state.contains(HAS_ITEM) && !state.get(HAS_ITEM)) {
            return; // do not render any item
        }

        ItemStack stack = entity.getStack(0);
        if (stack.isEmpty()) {
            return;
        }

        int packedLight = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos());
        renderStaticItem(stack, entity, matrices, vertexConsumers, packedLight, OverlayTexture.DEFAULT_UV);
    }

    private void renderStaticItem(ItemStack stack, PlateBlockEntity be,
                                  MatrixStack matrices, VertexConsumerProvider buffers,
                                  int packedLight, int packedOverlay) {
        var world = be.getWorld();
        if (world == null) {
            return;
        }

        // Grab baked model
        BakedModel model = itemRenderer.getModel(stack, world, null, 0);
        boolean is3d = model != null && model.hasDepth();
        float scale = is3d ? 1.0f : 1.2f;

        matrices.push();

        // Center just above the plate
        matrices.translate(0.45, 0.125, 0.4);

        // Rotate flat so it lies on the plate
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        matrices.scale(scale, scale, scale);

        itemRenderer.renderItem(
                stack,
                ModelTransformationMode.GROUND,
                packedLight,
                packedOverlay,
                matrices,
                buffers,
                world,
                0
        );

        matrices.pop();
    }
}
