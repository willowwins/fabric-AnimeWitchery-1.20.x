package net.willowins.animewitchery.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import net.willowins.animewitchery.ritual.RitualConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SkyRitualRenderer {
    private static final Identifier SKY_RITUAL_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_sky.png");
    private static final Identifier BARRIER_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_texture.png");
    private static final List<BlockPos> activeRitualPositions = new ArrayList<>();

    public static void addActiveRitual(BlockPos pos) {
        if (!activeRitualPositions.contains(pos)) {
            activeRitualPositions.add(pos);
        }
    }

    public static void removeActiveRitual(BlockPos pos) {
        activeRitualPositions.remove(pos);
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (activeRitualPositions.isEmpty()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }

        for (BlockPos ritualPos : activeRitualPositions) {
            renderRitualSky(matrices, client, ritualPos, tickDelta);
            renderBarrier(matrices, client, ritualPos, tickDelta);
        }
    }

    private static void renderRitualSky(MatrixStack matrices, MinecraftClient client, BlockPos ritualPos, float tickDelta) {
        // Only within 200 blocks
        double distanceSq = client.player.getBlockPos().getSquaredDistance(ritualPos);
        if (distanceSq > 40000.0) {
            return;
        }

        // Position and size
        float skyHeight = 70.0f;
        float size = 120.0f;
        float centerX = ritualPos.getX() + 0.5f;
        float centerZ = ritualPos.getZ() + 0.5f;
        float centerY = ritualPos.getY() + skyHeight;

        // Time-based rotation
        float time = client.world.getTime() + tickDelta;
        float rotation = time * 0.5f;

        // Camera-relative transform
        var camera = client.gameRenderer.getCamera();
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        // Render state for visibility
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, SKY_RITUAL_TEXTURE);

        matrices.push();
        matrices.translate(centerX - camX, centerY - camY, centerZ - camZ);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.translate(-size / 2, 0, -size / 2);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float a = 1.0f; // fully visible
        // Quad (horizontal, facing down/up), full UV
        buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, 0).texture(0.0f, 0.0f).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), size, 0, 0).texture(1.0f, 0.0f).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), size, 0, size).texture(1.0f, 1.0f).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(matrices.peek().getPositionMatrix(), 0, 0, size).texture(0.0f, 1.0f).color(1.0f, 1.0f, 1.0f, a).next();

        tessellator.draw();
        matrices.pop();

        // Restore state
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void renderBarrier(MatrixStack matrices, MinecraftClient client, BlockPos ritualPos, float tickDelta) {
        // Only within 200 blocks
        double distanceSq = client.player.getBlockPos().getSquaredDistance(ritualPos);
        if (distanceSq > 40000.0) {
            return;
        }

        // Get the barrier circle entity to access distance information
        if (client.world.getBlockEntity(ritualPos) instanceof BarrierCircleBlockEntity circleEntity) {
            // Get barrier distances (double the glyph distances)
            int northRadius = circleEntity.getNorthDistance() * 2;
            int southRadius = circleEntity.getSouthDistance() * 2;
            int eastRadius = circleEntity.getEastDistance() * 2;
            int westRadius = circleEntity.getWestDistance() * 2;

            // Early-out if nothing to render
            if (northRadius <= 0 && southRadius <= 0 && eastRadius <= 0 && westRadius <= 0) {
                return;
            }

            // Camera-relative transform
            var camera = client.gameRenderer.getCamera();
            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            // Y level to render the barrier at
            float baseY = ritualPos.getY() + 0.1f; // slightly above ground
            float wallHeight = 100.0f; // Height of the barrier walls

            // Time-based animation
            float time = client.world.getTime() + tickDelta;
            float pulse = (float) (Math.sin(time * 0.1) * 0.3 + 0.7); // Pulsing effect

            // Render state for barrier
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, BARRIER_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            // Alpha only (texture provides color)
            float a = 0.7f * pulse;

            // Check ritual configuration to determine barrier shape
            RitualConfiguration config = circleEntity.getRitualConfiguration();
            boolean shouldRenderCircular = false;
            if (config != null) {
                if (config.getRitualType() == RitualConfiguration.RitualType.EFFECT) {
                    // EFFECT rituals always render circular area
                    shouldRenderCircular = true;
                } else if (config.getRitualType() == RitualConfiguration.RitualType.BARRIER &&
                           config.getBarrierShape() == RitualConfiguration.BarrierShape.CIRCULAR) {
                    shouldRenderCircular = true;
                }
            }

            if (shouldRenderCircular) {
                renderCircularBarrier(matrices, buffer, ritualPos, baseY, wallHeight, a, camX, camY, camZ);
            } else {
                renderRectangularBarrier(matrices, buffer, ritualPos, northRadius, southRadius, eastRadius, westRadius,
                                         baseY, wallHeight, a, camX, camY, camZ);
            }

            tessellator.draw();

            // Restore state
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    private static void renderTexturedBarrierWall(MatrixStack matrices, BufferBuilder buffer, float x1, float z1, float x2, float z2,
                                                  float baseY, float height, float thickness, float a) {
        // Calculate wall direction
        float dx = x2 - x1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dz * dz);
        if (length == 0) return;

        // Normalize direction
        dx /= length;
        dz /= length;

        // Perpendicular vector for thickness
        float px = -dz;
        float pz = dx;

        // Calculate the four corners of the wall (two quads back-to-back for thickness)
        float x1a = x1 + px * thickness;
        float z1a = z1 + pz * thickness;
        float x1b = x1 - px * thickness;
        float z1b = z1 - pz * thickness;
        float x2a = x2 + px * thickness;
        float z2a = z2 + pz * thickness;
        float x2b = x2 - px * thickness;
        float z2b = z2 - pz * thickness;

        float y0 = baseY;
        float y1 = baseY + height;

        // Tile 1 repeat per block length/height
        float u0 = 0.0f;
        float u1 = length;     // repeats horizontally
        float v0 = 0.0f;
        float v1 = height;     // repeats vertically

        var mat = matrices.peek().getPositionMatrix();

        // Front face
        buffer.vertex(mat, x1a, y0, z1a).texture(u0, v0).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x2a, y0, z2a).texture(u1, v0).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x2a, y1, z2a).texture(u1, v1).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x1a, y1, z1a).texture(u0, v1).color(1.0f, 1.0f, 1.0f, a).next();

        // Back face (reverse winding)
        buffer.vertex(mat, x1b, y0, z1b).texture(u0, v0).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x1b, y1, z1b).texture(u0, v1).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x2b, y1, z2b).texture(u1, v1).color(1.0f, 1.0f, 1.0f, a).next();
        buffer.vertex(mat, x2b, y0, z2b).texture(u1, v0).color(1.0f, 1.0f, 1.0f, a).next();
    }

    /**
     * Renders a rectangular barrier using the original wall-based approach
     */
    private static void renderRectangularBarrier(MatrixStack matrices, BufferBuilder buffer, BlockPos ritualPos,
                                                int northRadius, int southRadius, int eastRadius, int westRadius,
                                                float baseY, float wallHeight, float a, double camX, double camY, double camZ) {
        // Compute extents using per-direction radii
        float centerX = ritualPos.getX() + 0.5f;
        float centerZ = ritualPos.getZ() + 0.5f;
        float minX = centerX - westRadius;
        float maxX = centerX + eastRadius;
        float minZ = centerZ - northRadius;
        float maxZ = centerZ + southRadius;

        // Render barrier walls as vertical quads (two-faced via thickness)
        float thickness = 0.25f;

        matrices.push();
        matrices.translate(-camX, -camY, -camZ);

        renderTexturedBarrierWall(matrices, buffer, minX, minZ, maxX, minZ, baseY, wallHeight, thickness, a); // North
        renderTexturedBarrierWall(matrices, buffer, minX, maxZ, maxX, maxZ, baseY, wallHeight, thickness, a); // South
        renderTexturedBarrierWall(matrices, buffer, maxX, minZ, maxX, maxZ, baseY, wallHeight, thickness, a); // East
        renderTexturedBarrierWall(matrices, buffer, minX, minZ, minX, maxZ, baseY, wallHeight, thickness, a); // West

        matrices.pop();
    }

    /**
     * Renders a circular barrier using multiple segments
     */
    private static void renderCircularBarrier(MatrixStack matrices, BufferBuilder buffer, BlockPos ritualPos,
                                             float baseY, float wallHeight, float a, double camX, double camY, double camZ) {
        // Get the barrier circle entity to access distance information
        if (MinecraftClient.getInstance().world.getBlockEntity(ritualPos) instanceof BarrierCircleBlockEntity circleEntity) {
            // Use the maximum distance from any direction as the radius
            int maxRadius = Math.max(Math.max(circleEntity.getNorthDistance(), circleEntity.getSouthDistance()), 
                                   Math.max(circleEntity.getEastDistance(), circleEntity.getWestDistance())) * 2;
            float radius = maxRadius;
            float centerX = ritualPos.getX() + 0.5f;
            float centerZ = ritualPos.getZ() + 0.5f;
            
            // Number of segments for the circle (more segments = smoother circle)
            int segments = 32;
            float angleStep = (float) (2 * Math.PI / segments);
            float thickness = 0.25f;

            matrices.push();
            matrices.translate(-camX, -camY, -camZ);

            // Render circular barrier as multiple wall segments
            for (int i = 0; i < segments; i++) {
                float angle1 = i * angleStep;
                float angle2 = (i + 1) * angleStep;
                
                // Calculate segment endpoints
                float x1 = centerX + (float) Math.cos(angle1) * radius;
                float z1 = centerZ + (float) Math.sin(angle1) * radius;
                float x2 = centerX + (float) Math.cos(angle2) * radius;
                float z2 = centerZ + (float) Math.sin(angle2) * radius;
                
                // Render this segment as a wall
                renderTexturedBarrierWall(matrices, buffer, x1, z1, x2, z2, baseY, wallHeight, thickness, a);
            }

            matrices.pop();
        }
    }
}
