package net.willowins.animewitchery.client.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.util.Identifier;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.gl.VertexBuffer;
import java.util.Random;

public class ParadiseLostSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    private static final Identifier END_SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
    private static final Identifier STAR_TEXTURE = new Identifier("textures/entity/end_rod.png");
    private VertexBuffer starsBuffer;

    public ParadiseLostSkyRenderer() {
    }

    private void generateStars() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        if (this.starsBuffer != null) {
            this.starsBuffer.close();
        }

        this.starsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        BufferBuilder.BuiltBuffer builtBuffer = this.renderStars(bufferBuilder);
        this.starsBuffer.bind();
        this.starsBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    private BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer) {
        Random random = new Random(10842L);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < 1500; ++i) {
            double d = (double) (random.nextFloat() * 2.0F - 1.0F);
            double e = (double) (random.nextFloat() * 2.0F - 1.0F);
            double f = (double) (random.nextFloat() * 2.0F - 1.0F);
            double g = 0.15 + (double) (random.nextFloat() * 0.1F);
            double h = d * d + e * e + f * f;
            if (h < 1.0 && h > 0.01) {
                h = 1.0 / Math.sqrt(h);
                d *= h;
                e *= h;
                f *= h;
                double j = d * 100.0;
                double k = e * 100.0;
                double l = f * 100.0;
                double m = Math.atan2(d, f);
                double n = Math.sin(m);
                double o = Math.cos(m);
                double p = Math.atan2(Math.sqrt(d * d + f * f), e);
                double q = Math.sin(p);
                double r = Math.cos(p);
                double s = random.nextDouble() * 3.141592653589793 * 2.0;
                double t = Math.sin(s);
                double u = Math.cos(s);

                for (int v = 0; v < 4; ++v) {
                    double x = (double) ((v & 2) - 1) * g;
                    double y = (double) ((v + 1 & 2) - 1) * g;
                    double aa = x * u - y * t;
                    double ab = y * u + x * t;
                    double ac = aa * q + 0.0 * r;
                    double ad = 0.0 * q - aa * r;
                    double ae = ad * n - ab * o;
                    double af = ab * n + ad * o;

                    // Map UVs: (0,0), (0,1), (1,1), (1,0) based on vertex index logic
                    float uTex = ((v & 2) == 0) ? 0.0f : 1.0f;
                    float vTex = ((v + 1 & 2) == 0) ? 0.0f : 1.0f;

                    buffer.vertex(j + ae, k + ac, l + af).texture(uTex, vTex).next();
                }
            }
        }

        return buffer.end();
    }

    @Override
    public void render(WorldRenderContext context) {
        MatrixStack matrices = context.matrixStack();
        Matrix4f projectionMatrix = context.projectionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        // 1. Render End Skybox
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, END_SKY_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        for (int i = 0; i < 6; ++i) {
            matrices.push();
            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            }

            if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            }

            if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
            }

            if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
            tessellator.draw();
            matrices.pop();
        }

        // 2. Render Stars
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // White stars
        BackgroundRenderer.clearFog();

        if (this.starsBuffer == null) {
            this.generateStars();
        }

        if (this.starsBuffer != null) {
            this.starsBuffer.bind();
            this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix,
                    GameRenderer.getPositionTexProgram());
            VertexBuffer.unbind();
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
