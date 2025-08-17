package net.willowins.animewitchery.client.shader;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.util.VoidPhaseUtil;

public class VoidBoundShaderRenderer {
    private static final Identifier VOID_BOUND_SHADER = new Identifier(AnimeWitchery.MOD_ID, "void_bound");
    private static final Identifier VOID_BOUND_PIPELINE = new Identifier(AnimeWitchery.MOD_ID, "void_bound_pipeline");

    private static boolean initialized = false;
    private static boolean addedToPostProcessingPipeline = false;
    private static float totalTime = 0.0f;

    public static void init() {
        if (initialized) return;
        

        WorldRenderEvents.END.register(VoidBoundShaderRenderer::onWorldRenderEnd);
        initialized = true;
    }
    
    private static void onWorldRenderEnd(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        // Update total time for animations
        totalTime += client.getTickDelta();
        
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

            // Add the pipeline if it's not already added
            if (!addedToPostProcessingPipeline) {
                VeilRenderSystem.renderer()
                        .getPostProcessingManager()
                        .add(VOID_BOUND_PIPELINE);
                addedToPostProcessingPipeline = true;
            }

            // Set uniforms for the shader when pipeline is active
            setShaderUniforms();
        }


    }

    private static void setShaderUniforms() {
        // Get the shader program and set uniforms
        ShaderProgram shader = VeilRenderSystem.setShader(VOID_BOUND_SHADER);
        if (shader != null) {
            // Set texel size uniform
            shader.setVector("uTexelSize",
                    1.0f / MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    1.0f / MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
            
            // Set time uniform for animations
            shader.setFloat("uTime", totalTime);
            
            // Get current player and void phase for blending
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null && client.player.hasStatusEffect(ModEffect.VOID_BOUND)) {
                var effect = client.player.getStatusEffect(ModEffect.VOID_BOUND);
                float voidPhase = VoidPhaseUtil.computePhase(client.player.age, effect.getAmplifier());
                shader.setFloat("uVoidPhase", voidPhase);
            } else {
                shader.setFloat("uVoidPhase", 0.0f);
            }
            
            // Unbind shader after setting uniforms
            ShaderProgram.unbind();
        }
    }

    
    



    
    public static void cleanup() {
        initialized = false;
    }
}
