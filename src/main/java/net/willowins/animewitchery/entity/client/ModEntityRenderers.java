package net.willowins.animewitchery.entity.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.willowins.animewitchery.entity.ModEntities;

public class ModEntityRenderers implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register invisible placeholder renderer
        EntityRendererRegistry.register(ModEntities.KINETIC_BLADE_HITBOX, EmptyEntityRenderer::new);
    }
}
