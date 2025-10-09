package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;

public class GrandShulkerBoxRenderer implements BlockEntityRenderer<GrandShulkerBoxBlockEntity> {
    
    private final ShulkerBoxBlockEntityRenderer vanillaRenderer;
    
    public GrandShulkerBoxRenderer(BlockEntityRendererFactory.Context context) {
        this.vanillaRenderer = new ShulkerBoxBlockEntityRenderer(context);
    }

    @Override
    public void render(GrandShulkerBoxBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Create a temporary vanilla shulker box block entity to use with the vanilla renderer
        // This is a workaround to use the vanilla rendering system
        if (entity.getWorld() != null) {
            BlockState state = entity.getWorld().getBlockState(entity.getPos());
            if (state.getBlock() instanceof ShulkerBoxBlock) {
                // Use the vanilla renderer to render our block
                // We'll create a temporary vanilla shulker box entity for rendering
                net.minecraft.block.entity.ShulkerBoxBlockEntity tempEntity = 
                    new net.minecraft.block.entity.ShulkerBoxBlockEntity(
                        DyeColor.PURPLE, // Default color
                        entity.getPos(), 
                        state
                    );
                
                // Copy the inventory from our entity to the temp entity
                for (int i = 0; i < Math.min(entity.size(), tempEntity.size()); i++) {
                    tempEntity.setStack(i, entity.getStack(i));
                }
                
                // Render using the vanilla renderer
                vanillaRenderer.render(tempEntity, tickDelta, matrices, vertexConsumers, light, overlay);
            }
        }
    }
}
