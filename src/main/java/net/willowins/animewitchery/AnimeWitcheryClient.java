package net.willowins.animewitchery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.renderer.ActiveBindingSpellRenderer;
import net.willowins.animewitchery.block.entity.renderer.ActiveEffigyFountainRenderer;
import net.willowins.animewitchery.block.entity.renderer.AlchemyTableRenderer;
import net.willowins.animewitchery.block.entity.renderer.BindingSpellRenderer;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.entity.client.render.NeedleModel;
import net.willowins.animewitchery.entity.client.render.NeedleProjectileRenderer;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.particle.ShockwaveParticle;
import net.willowins.animewitchery.screen.*;


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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AUTO_CRAFTER_BLOCK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY, (context) -> new ActiveEffigyFountainRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY, (context) -> new ActiveBindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.BINDING_SPELL_BLOCK_ENTITY, (context) -> new BindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY, (context) -> new AlchemyTableRenderer());

        EntityRendererRegistry.register(ModEntities.NEEDLE_PROJECTILE, NeedleProjectileRenderer::new);


        ParticleFactoryRegistry.getInstance().register(ModParticles.LASER_PARTICLE, ShockwaveParticle.Factory::new);
        ScreenRegistry.register(ModScreenHandlers.PLAYER_USE_DISPENSER_SCREEN_HANDLER, PlayerUseDispenserScreen::new);
        ScreenRegistry.register(ModScreenHandlers.AUTO_CRAFTER_SCREEN_HANDLER, AutoCrafterScreen::new);
        ScreenRegistry.register(ModScreenHandlers.ITEM_ACTION_SCREEN_HANDLER, ItemActionScreen::new);
        ScreenRegistry.register(ModScreenHandlers.BLOCK_MINER_SCREEN_HANDLER, BlockMinerScreen::new);
        ScreenRegistry.register(ModScreenHandlers.BLOCK_PLACER_SCREEN_HANDLER, BlockPlacerScreen::new);
        ScreenRegistry.register(ModScreenHandlers.GROWTH_ACCELERATOR_SCREEN_HANDLER, GrowthAcceleratorScreen::new);

        ModPackets.registerS2CPackets();

    }
}
