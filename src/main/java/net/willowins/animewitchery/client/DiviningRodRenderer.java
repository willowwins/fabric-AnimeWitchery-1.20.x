package net.willowins.animewitchery.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;

import java.util.HashSet;
import java.util.Set;

public class DiviningRodRenderer {
    private static final Set<BlockPos> TARGETS = new HashSet<>();
    private static long expiryTime = 0;
    private static final long DURATION = 10000; // 10 seconds

    public static void highlight(Set<BlockPos> ores) {
        TARGETS.clear();
        TARGETS.addAll(ores);
        expiryTime = System.currentTimeMillis() + DURATION;
    }

    public static void register() {
        WorldRenderEvents.LAST.register(DiviningRodRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        if (TARGETS.isEmpty() || System.currentTimeMillis() > expiryTime) {
            TARGETS.clear();
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = context.camera();
        if (camera == null)
            return;

        Vec3d cameraPos = camera.getPos();
        MatrixStack matrices = context.matrixStack();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest(); // See through walls
        RenderSystem.disableCull();
        RenderSystem.lineWidth(2.0f);

        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        for (BlockPos pos : TARGETS) {
            Box box = new Box(pos);
            drawBoxOutline(matrices, buffer, box, 1.0f, 0.84f, 0.0f, 1.0f); // Gold color
        }

        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth(1.0f);
        matrices.pop();
    }

    private static void drawBoxOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red,
            float green, float blue, float alpha) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        // Bottom
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();

        // Top
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();

        // Sides
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(red, green, blue, alpha).next();
    }
}
