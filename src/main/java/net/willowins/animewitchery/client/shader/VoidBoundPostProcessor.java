package net.willowins.animewitchery.client.shader;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;
import net.willowins.animewitchery.AnimeWitchery;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.JsonEffectShaderProgram;

public class VoidBoundPostProcessor extends PostProcessor {
    
    public static final VoidBoundPostProcessor INSTANCE = new VoidBoundPostProcessor();
    
    @Override
    public Identifier getPostChainLocation() {
        return new Identifier(AnimeWitchery.MOD_ID, "void_bound_post");
    }
    
    @Override
    public void beforeProcess(MatrixStack matrixStack) {
        // The effect directly controls the active state, no need to check here
    }
    

    @Override
    public void afterProcess() {
        // Called after processing the shader
    }
    
    // Method to get current void phase from the player
    public static float getCurrentVoidPhase() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            PlayerEntity player = client.player;
            if (player.hasStatusEffect(net.willowins.animewitchery.effect.ModEffect.VOID_BOUND)) {
                // Calculate void phase based on player age and effect amplifier
                int amplifier = player.getStatusEffect(net.willowins.animewitchery.effect.ModEffect.VOID_BOUND).getAmplifier();
                float phaseSpeed = 0.05f + (amplifier * 0.02f);
                float phase = (player.age * phaseSpeed) % (2.0f * (float)Math.PI);
                return (float)(Math.sin(phase) + 1.0) * 0.5f;
            }
        }
        return 0.0f; // No void effect active
    }
}
