package net.willowins.animewitchery.item.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;

public final class ObeliskBuiltinItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    // Re-create each frame at the player's position to ensure valid lighting and chunk context

    @Override
    public void render(ItemStack stack,
                       ModelTransformationMode mode,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light,
                       int overlay) {
        matrices.push();
        // Center and scale to fit item contexts
        matrices.translate(0.5f, 0.5f, 0.5f);
        switch (mode) {
            case GUI -> {
                matrices.translate(0.1, -0.3f, 0);
                matrices.scale(0.3f, 0.3f, 0.3f);
                // Flip Y for GUI to match item space, and face camera
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            }
            case GROUND -> matrices.scale(0.3f, 0.3f, 0.3f);
            case FIXED -> matrices.scale(0.25f, 0.25f, 0.25f);
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                matrices.scale(0.25f, 0.25f, 0.25f);
            }
            case HEAD, NONE -> matrices.scale(0.25f, 0.25f, 0.25f);
        }

        // Create a transient block entity at the player's position for proper lighting
        var client = MinecraftClient.getInstance();
        var world = client.world;
        var player = client.player;
        BlockPos renderPos = player != null ? player.getBlockPos() : BlockPos.ORIGIN;

        ObeliskBlockEntity be = new ObeliskBlockEntity(renderPos, ModBlocks.OBELISK.getDefaultState());
        be.setWorld(world);

        client.getBlockEntityRenderDispatcher().render(be, 0.0f, matrices, vertexConsumers);

        matrices.pop();
    }
}


