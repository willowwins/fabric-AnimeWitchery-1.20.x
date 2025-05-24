package net.willowins.animewitchery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.custom.EffigyFountainBlock;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.renderer.ActiveBindingSpellRenderer;
import net.willowins.animewitchery.block.entity.renderer.ActiveEffigyFountainRenderer;
import net.willowins.animewitchery.block.entity.renderer.AlchemyTableRenderer;
import net.willowins.animewitchery.block.entity.renderer.BindingSpellRenderer;
import net.willowins.animewitchery.networking.ModPackets;

public class AnimeWitcheryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SILVER_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACTIVE_BINDING_SPELL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BINDING_SPELL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALCHEMY_TABLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SILVER_TRAPDOOR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STRAWBERRY_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LEMON_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLOAT_BLOCK, RenderLayer.getCutout());


        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY, (context) -> new ActiveEffigyFountainRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY, (context) -> new ActiveBindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.BINDING_SPELL_BLOCK_ENTITY, (context) -> new BindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY, (context) -> new AlchemyTableRenderer());

        ModPackets.registerS2CPackets();

    }
}
