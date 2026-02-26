package net.willowins.animewitchery.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import org.joml.Matrix4f;

public class PocketGridRenderer {
    private static final double VISIBILITY_DISTANCE = 80.0; // Starts fading in at 80 blocks

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || !client.world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY)) {
            return;
        }

        // Animation Logic
        // 29 Frames: subdivision1.png to subdivision29.png
        // 50ms per frame = 20fps
        long time = System.currentTimeMillis();
        int frame = (int) ((time / 50L) % 29) + 1;
        Identifier texture = new Identifier(AnimeWitchery.MOD_ID, "textures/block/subdivision" + frame + ".png");

        Vec3d cameraPos = context.camera().getPos();
        double camX = cameraPos.getX();
        double camZ = cameraPos.getZ();

        // Calculate current 2D cell (Centered)
        // Cell Center = k * 1000
        // Boundary = k * 1000 +/- 500

        int currentCol = (int) Math.floor((camX + 500) / 1000.0);
        int currentRow = (int) Math.floor((camZ + 500) / 1000.0);

        // Render walls for current cell and neighbors (range +/- 2)
        int renderRange = 2;

        for (int c = currentCol - renderRange; c <= currentCol + renderRange; c++) {
            double centerX = c * 1000.0;

            // Walls at Center +/- 500
            renderWall(context, centerX - 500, camX, camZ, texture);
            renderWall(context, centerX + 500, camX, camZ, texture);
        }

        for (int r = currentRow - renderRange; r <= currentRow + renderRange; r++) {
            double centerZ = r * 1000.0;

            // Walls at Center +/- 500
            renderZWall(context, 0, centerZ - 500, camX, camZ, texture);
            renderZWall(context, 0, centerZ + 500, camX, camZ, texture);
        }
    }

    private static void renderWall(WorldRenderContext context, double x, double camX, double camZ, Identifier texture) {
        // Distance from player X to wall X
        double dist = Math.abs(x - camX);
        if (dist > VISIBILITY_DISTANCE)
            return;

        float alpha = (float) (1.0 - (dist / VISIBILITY_DISTANCE));
        alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);

        // Render large plane along Z
        double minZ = camZ - 1000;
        double maxZ = camZ + 1000;
        double minY = -64;
        double maxY = 320;

        renderPlane(context, x, x, minY, maxY, minZ, maxZ, camX, camZ, true, alpha, texture);
    }

    private static void renderZWall(WorldRenderContext context, double centerX, double z, double camX, double camZ,
            Identifier texture) {
        // Distance from player Z to wall Z
        double dist = Math.abs(z - camZ);
        if (dist > VISIBILITY_DISTANCE)
            return;

        float alpha = (float) (1.0 - (dist / VISIBILITY_DISTANCE));
        alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);

        // Render large plane along X
        double minX = camX - 1000;
        double maxX = camX + 1000;
        double minY = -64;
        double maxY = 320;

        renderPlane(context, minX, maxX, minY, maxY, z, z, camX, camZ, false, alpha, texture);
    }

    private static void renderPlane(WorldRenderContext context, double minX, double maxX, double minY, double maxY,
            double minZ, double maxZ, double camX, double camZ, boolean isXWall, float alpha, Identifier texture) {

        if (alpha <= 0.0f)
            return;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        MatrixStack matrices = context.matrixStack();

        matrices.push();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float uScale = 1.0f; // 1.0 creates block-aligned tiling for 16x16 textures
        float vScale = 1.0f;

        double x1 = minX - camX;
        double x2 = maxX - camX;
        double y1 = minY - context.camera().getPos().y;
        double y2 = maxY - context.camera().getPos().y;
        double z1 = minZ - camZ;
        double z2 = maxZ - camZ;

        Matrix4f mat = matrices.peek().getPositionMatrix();

        RenderSystem.disableCull();

        if (isXWall) {
            float u1 = (float) minZ * uScale;
            float u2 = (float) maxZ * uScale;
            float v1 = (float) minY * vScale;
            float v2 = (float) maxY * vScale;

            buffer.vertex(mat, (float) x1, (float) y1, (float) z1).texture(u1, v2).next();
            buffer.vertex(mat, (float) x1, (float) y2, (float) z1).texture(u1, v1).next();
            buffer.vertex(mat, (float) x1, (float) y2, (float) z2).texture(u2, v1).next();
            buffer.vertex(mat, (float) x1, (float) y1, (float) z2).texture(u2, v2).next();
        } else {
            float u1 = (float) minX * uScale;
            float u2 = (float) maxX * uScale;
            float v1 = (float) minY * vScale;
            float v2 = (float) maxY * vScale;

            buffer.vertex(mat, (float) x1, (float) y1, (float) z1).texture(u1, v2).next();
            buffer.vertex(mat, (float) x1, (float) y2, (float) z1).texture(u1, v1).next();
            buffer.vertex(mat, (float) x2, (float) y2, (float) z1).texture(u2, v1).next();
            buffer.vertex(mat, (float) x2, (float) y1, (float) z1).texture(u2, v2).next();
        }

        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }
}
