package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;

public class BarrierCircleRenderer implements BlockEntityRenderer<BarrierCircleBlockEntity> {
    
    // Texture identifiers for different stages
    private static final Identifier BASIC_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_basic.png");
    private static final Identifier DEFINED_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_defined.png");
    private static final Identifier COMPLETE_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_complete.png");

    public BarrierCircleRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BarrierCircleBlockEntity entity, float tickDelta, MatrixStack matrices, 
                      VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // Get the current stage to determine which texture to use
        BarrierCircleBlockEntity.CircleStage stage = entity.getStage();
        Identifier textureToUse;
        
        switch (stage) {
            case BASIC:
                textureToUse = BASIC_CIRCLE_TEXTURE;
                break;
            case DEFINED:
                textureToUse = DEFINED_CIRCLE_TEXTURE;
                break;
            case COMPLETE:
                textureToUse = COMPLETE_CIRCLE_TEXTURE;
                break;
            default:
                textureToUse = BASIC_CIRCLE_TEXTURE;
        }
                
        // Render the circle texture as a flat overlay on the ground
        renderCircleOverlay(matrices, vertexConsumers, textureToUse, light, overlay);
    }
    
    private void renderCircleOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                   Identifier texture, int light, int overlay) {
        
        // Get the vertex consumer for the texture with proper texture binding
        var renderLayer = RenderLayer.getEntityTranslucentCull(texture);
        var vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                
        // Create a flat quad slightly above the ground (Y + 0.01)
        float y = 0.01f;
        
        // Define the quad vertices (11x11 block size) - centered on the block
        float minX = -5.5f;
        float maxX = 5.5f;  // 11 blocks wide
        float minZ = -5.5f;
        float maxZ = 5.5f;  // 11 blocks deep
        
        // UV coordinates for the texture (128x128 texture will be automatically scaled)
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 1.0f;  // Flipped V coordinates to fix upside-down texture
        float maxV = 0.0f;  // Flipped V coordinates to fix upside-down texture
        
        // Render the quad
        matrices.push();
        
        // Center the circle on the block by offsetting the rendering position
        matrices.translate(0.5f, 0, 0.5f);
        
        // Top-left vertex (first)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), minX, y, maxZ)
                .color(255, 255, 255, 255)
                .texture(minU, minV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Top-right vertex (second)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), maxX, y, maxZ)
                .color(255, 255, 255, 255)
                .texture(maxU, minV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Bottom-right vertex (third)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), maxX, y, minZ)
                .color(255, 255, 255, 255)
                .texture(maxU, maxV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Bottom-left vertex (fourth)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), minX, y, minZ)
                .color(255, 255, 255, 255)
                .texture(minU, maxV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        matrices.pop();
    }
} 