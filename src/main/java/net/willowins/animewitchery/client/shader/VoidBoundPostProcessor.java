package net.willowins.animewitchery.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.util.VoidPhaseUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class VoidBoundPostProcessor {
    private static final Identifier VOID_BOUND_SHADER = new Identifier(AnimeWitchery.MOD_ID, "void_bound");
    
    private static boolean initialized = false;
    private static boolean isActive = false;
    private static float totalTime = 0.0f;
    private static PostEffectProcessor postProcessor = null;

    public static void init() {
        if (initialized) return;
        
        WorldRenderEvents.END.register(VoidBoundPostProcessor::onWorldRenderEnd);
        HudRenderCallback.EVENT.register(VoidBoundPostProcessor::onHudRender);
        initialized = true;
    }
    
    private static void onWorldRenderEnd(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        
        // Update total time for animations
        totalTime = (totalTime + client.getTickDelta()) % 10000.0f; // Keep bounded
        
        PlayerEntity player = client.player;
        boolean hasVoidBound = player.hasStatusEffect(ModEffect.VOID_BOUND);
        
        if (hasVoidBound && !isActive) {
            // Activate the effect
            activatePostProcessor(client);
        } else if (!hasVoidBound && isActive) {
            // Deactivate the effect
            deactivatePostProcessor();
        }
        
        if (isActive && postProcessor != null) {
            // Update shader uniforms
            updateShaderUniforms(client, player);
        }
    }
    
    private static void activatePostProcessor(MinecraftClient client) {
        try {
            // Try to load the post-processor
            postProcessor = new PostEffectProcessor(client.getTextureManager(), 
                    client.getResourceManager(), client.getFramebuffer(), VOID_BOUND_SHADER);
            postProcessor.setupDimensions(client.getWindow().getScaledWidth(), 
                    client.getWindow().getScaledHeight());
            isActive = true;
        } catch (Exception e) {
            AnimeWitchery.LOGGER.warn("Failed to load void bound post-processor: " + e.getMessage());
            isActive = false;
        }
    }
    
    private static void deactivatePostProcessor() {
        if (postProcessor != null) {
            postProcessor.close();
            postProcessor = null;
        }
        isActive = false;
    }
    
    private static void updateShaderUniforms(MinecraftClient client, PlayerEntity player) {
        if (postProcessor == null) return;
        
        try {
            // Note: PostEffectProcessor in 1.20.1 doesn't expose shader uniforms directly
            // The uniforms are set through the shader JSON file and handled internally
            // We'll rely on the shader to use default values or compute them internally
            var effect = player.getStatusEffect(ModEffect.VOID_BOUND);
            if (effect != null) {
                // The shader will need to compute these values internally
                // since we can't set uniforms directly on PostEffectProcessor
                AnimeWitchery.LOGGER.debug("Void bound effect active, phase: " + 
                    VoidPhaseUtil.computePhase(player.age, effect.getAmplifier()));
            }
        } catch (Exception e) {
            AnimeWitchery.LOGGER.warn("Failed to update void bound shader uniforms: " + e.getMessage());
        }
    }
    
    private static void onHudRender(net.minecraft.client.gui.DrawContext context, float tickDelta) {
        if (isActive && postProcessor != null) {
            try {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                
                postProcessor.render(tickDelta);
                
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            } catch (Exception e) {
                AnimeWitchery.LOGGER.warn("Failed to render void bound post-processor: " + e.getMessage());
            }
        }
    }
    
    public static void render(float tickDelta) {
        if (isActive && postProcessor != null) {
            try {
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                
                postProcessor.render(tickDelta);
                
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();
            } catch (Exception e) {
                AnimeWitchery.LOGGER.warn("Failed to render void bound post-processor: " + e.getMessage());
            }
        }
    }
    
    public static void cleanup() {
        deactivatePostProcessor();
        initialized = false;
    }
}
