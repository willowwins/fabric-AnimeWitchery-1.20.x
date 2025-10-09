package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.block.BlockState;
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
        if (entity.getWorld() != null) {
            BlockState state = entity.getWorld().getBlockState(entity.getPos());
            
            // Get the color from the block type
            DyeColor color = DyeColor.PURPLE; // Default fallback
            if (state.getBlock() instanceof net.willowins.animewitchery.block.custom.GrandShulkerBoxBlock grandBox) {
                color = grandBox.getColor();
            }
            
            // Create a temporary vanilla shulker box entity for rendering
            net.minecraft.block.entity.ShulkerBoxBlockEntity tempEntity = 
                new net.minecraft.block.entity.ShulkerBoxBlockEntity(
                    color, // Use the actual color from the block state
                    entity.getPos(), 
                    state
                );
            
            // Copy the inventory from our entity to the temp entity (only first 27 slots)
            for (int i = 0; i < Math.min(entity.size(), 27); i++) {
                tempEntity.setStack(i, entity.getStack(i));
            }
            
            // Render using the vanilla renderer
            vanillaRenderer.render(tempEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }
}
