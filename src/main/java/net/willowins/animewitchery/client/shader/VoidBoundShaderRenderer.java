package net.willowins.animewitchery.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.util.VoidPhaseUtil;

public class VoidBoundShaderRenderer {
    private static final Identifier VOID_BOUND_SHADER = new Identifier(AnimeWitchery.MOD_ID, "void_bound");
    private static final Identifier VOID_BOUND_PIPELINE = new Identifier(AnimeWitchery.MOD_ID, "void_bound_pipeline");

    private static Framebuffer effectFramebuffer;
    private static boolean initialized = false;
    private static boolean addedToPostProcessingPipeline = false;

    public static void init() {
        if (initialized) return;
        

        WorldRenderEvents.END.register(VoidBoundShaderRenderer::onWorldRenderEnd);
        initialized = true;
    }
    
    private static void onWorldRenderEnd(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        PlayerEntity player = client.player;
        if (!player.hasStatusEffect(ModEffect.VOID_BOUND)) {

            if (addedToPostProcessingPipeline) {
                VeilRenderSystem.renderer()
                        .getPostProcessingManager()
                        .remove(VOID_BOUND_PIPELINE);
                addedToPostProcessingPipeline = false;
            }

            return;
        }else {

            if (!addedToPostProcessingPipeline) {
                VeilRenderSystem.renderer()
                        .getPostProcessingManager()
                        .add(VOID_BOUND_PIPELINE);
                addedToPostProcessingPipeline = true;
            }
        }

        var effect = player.getStatusEffect(ModEffect.VOID_BOUND);
        // Check if we're actually in the void phase (not just the effect being active)
        float voidPhase = VoidPhaseUtil.computePhase(player.age, effect.getAmplifier()); // 0..1
        if (voidPhase < 0.99f) return; // Only activate shader during void phase

        // Apply the void bound shader effect
        applyVoidBoundEffect();
    }

    private static void applyVoidBoundEffect() {
        // Get the shader program
        ShaderProgram shader = VeilRenderSystem.setShader(VOID_BOUND_SHADER);
        if (shader == null) return;

        // Get the main framebuffer
        Framebuffer mainFramebuffer = MinecraftClient.getInstance().getFramebuffer();

        // Create effect framebuffer if needed
        if (effectFramebuffer == null ||
            effectFramebuffer.textureWidth != mainFramebuffer.textureWidth ||
            effectFramebuffer.textureHeight != mainFramebuffer.textureHeight) {
            if (effectFramebuffer != null) {
                effectFramebuffer.delete();
            }
            effectFramebuffer = new SimpleFramebuffer(mainFramebuffer.textureWidth, mainFramebuffer.textureHeight, false, false);
        }

        // Store current framebuffer
        Framebuffer currentFramebuffer = MinecraftClient.getInstance().getFramebuffer();

        // Bind the effect framebuffer
        effectFramebuffer.beginWrite(true);

        // Apply the shader
        shader.bind();
        shader.setVector("uTexelSize",
                1.0f / currentFramebuffer.textureWidth,
                1.0f / currentFramebuffer.textureHeight
        );

        // Render fullscreen quad with the shader
        renderFullscreenQuad();
        
        // Unbind shader
        ShaderProgram.unbind();
        
        // Copy result back to main framebuffer
        currentFramebuffer.beginWrite(true);
        effectFramebuffer.draw(currentFramebuffer.textureWidth, currentFramebuffer.textureHeight);
    }

    private static void renderFullscreenQuad() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        var fb = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.viewport(0, 0, fb.textureWidth, fb.textureHeight);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        // IMPORTANT: match the shader — POSITION only (no UVs)
        buf.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION);

        // 6 vertices for two triangles; VS uses gl_VertexID to place them anyway
        buf.vertex(-1.0f, -1.0f, 1.0f).next();
        buf.vertex( 1.0f, -1.0f, 1.0f).next();
        buf.vertex(-1.0f,  1.0f, 1.0f).next();
        buf.vertex( 1.0f, -1.0f, 1.0f).next();
        buf.vertex( 1.0f,  1.0f, 1.0f).next();
        buf.vertex(-1.0f,  1.0f, 1.0f).next();

        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }


    
    public static void cleanup() {
        if (effectFramebuffer != null) {
            effectFramebuffer.delete();
            effectFramebuffer = null;
        }
        initialized = false;
    }
}
