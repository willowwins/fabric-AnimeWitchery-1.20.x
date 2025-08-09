package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.BarrierDistanceGlyphBlockEntity;

public class BarrierDistanceGlyphRenderer implements BlockEntityRenderer<BarrierDistanceGlyphBlockEntity> {
    
    // Texture identifier for the distance glyph
    private static final Identifier DISTANCE_GLYPH_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_distance_glyph.png");

    public BarrierDistanceGlyphRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BarrierDistanceGlyphBlockEntity entity, float tickDelta, MatrixStack matrices, 
                      VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // Render the distance glyph texture as a flat overlay on the ground
        renderGlyphOverlay(matrices, vertexConsumers, DISTANCE_GLYPH_TEXTURE, light, overlay);
    }
    
    private void renderGlyphOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                   Identifier texture, int light, int overlay) {
        
        // Get the vertex consumer for the texture with proper texture binding
        var renderLayer = RenderLayer.getEntityTranslucentCull(texture);
        var vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                
        // Create a flat quad slightly above the ground (Y + 0.01)
        float y = 0.01f;
        
        // Define the quad vertices (11x11 block size) - centered on the block
        float minX = -1.5f;
        float maxX = 1.5f;  // 3 blocks wide
        float minZ = -1.5f;
        float maxZ = 1.5f;  // 3 blocks deep
        
        // UV coordinates for the texture
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 1.0f;  // Flipped V coordinates to fix upside-down texture
        float maxV = 0.0f;  // Flipped V coordinates to fix upside-down texture
        
        // Render the quad
        matrices.push();
        
        // Center the glyph on the block by offsetting the rendering position
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
