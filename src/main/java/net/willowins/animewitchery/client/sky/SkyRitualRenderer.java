package net.willowins.animewitchery.client.sky;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.AnimeWitchery;

import java.util.ArrayList;
import java.util.List;

public class SkyRitualRenderer {
    private static final Identifier SKY_RITUAL_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_sky.png");
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
}
