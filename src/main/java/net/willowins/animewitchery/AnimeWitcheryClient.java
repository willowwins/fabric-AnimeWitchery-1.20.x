package net.willowins.animewitchery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.renderer.*;
import net.willowins.animewitchery.block.entity.renderer.GrandShulkerBoxRenderer;
import net.willowins.animewitchery.client.DebugScreenInterceptor;
import net.willowins.animewitchery.client.render.FlightElytraRenderLayer;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.entity.client.render.NeedleProjectileRenderer;
import net.willowins.animewitchery.entity.client.render.VoidWispRenderer;
import net.willowins.animewitchery.entity.client.KamikazeRitualRenderer;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.ManaRocketItem;
import net.willowins.animewitchery.item.custom.ObeliskCompassAnglePredicateProvider;
import net.willowins.animewitchery.item.custom.ObeliskCompassItem;
import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.particle.ShockwaveParticle;
import net.willowins.animewitchery.screen.*;
import net.willowins.animewitchery.client.sky.SkyRitualRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.willowins.animewitchery.item.renderer.ObeliskBuiltinItemRenderer;
import net.willowins.animewitchery.client.shader.VoidBoundShaderRenderer;



public class AnimeWitcheryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SILVER_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACTIVE_EFFIGY_FOUNTAIN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACTIVE_BINDING_SPELL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BINDING_SPELL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALCHEMY_TABLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PLATE_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OBELISK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ACTIVE_OBELISK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BARRIER_CIRCLE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BARRIER_DISTANCE_GLYPH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SILVER_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOSS_OBELISK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUARDIAN_STATUE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PILLAR, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STRAWBERRY_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LEMON_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLOAT_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AUTO_CRAFTER_BLOCK, RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY, (context) -> new ActiveEffigyFountainRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY, (context) -> new ActiveBindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.BINDING_SPELL_BLOCK_ENTITY, (context) -> new BindingSpellRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY, AlchemyTableRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OBELISK_BLOCK_ENTITY, (context) -> new ObeliskRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY, (context) -> new ActiveObeliskRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY, (context) -> new BarrierCircleRenderer(context));
        BlockEntityRendererRegistry.register(ModBlockEntities.BARRIER_DISTANCE_GLYPH_BLOCK_ENTITY, (context) -> new BarrierDistanceGlyphRenderer(context));
        BlockEntityRendererRegistry.register(ModBlockEntities.BOSS_OBELISK_BLOCK_ENTITY, (context) -> new BossObeliskRenderer());
        BlockEntityRendererRegistry.register(ModBlockEntities.GUARDIAN_STATUE_BLOCK_ENTITY, GuardianStatueRenderer::new);
                BlockEntityRendererRegistry.register(ModBlockEntities.PLATE_BLOCK_ENTITY, PlateRenderer::new);
                BlockEntityRendererRegistry.register(ModBlocks.GRAND_SHULKER_BOX_ENTITY, GrandShulkerBoxRenderer::new);

        EntityRendererRegistry.register(ModEntities.NEEDLE_PROJECTILE, NeedleProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.VOID_WISP, VoidWispRenderer::new);
        EntityRendererRegistry.register(ModEntities.KAMIKAZE_RITUAL, KamikazeRitualRenderer::new);

        ObeliskCompassAnglePredicateProvider.registerModelPredicate(ModItems.OBELISK_COMPASS);
        System.out.println("[AnimeWitchery] Registered ObeliskCompass angle predicate.");

        ParticleFactoryRegistry.getInstance().register(ModParticles.LASER_PARTICLE, ShockwaveParticle.Factory::new);
        ScreenRegistry.register(ModScreenHandlers.PLAYER_USE_DISPENSER_SCREEN_HANDLER, PlayerUseDispenserScreen::new);
        ScreenRegistry.register(ModScreenHandlers.AUTO_CRAFTER_SCREEN_HANDLER, AutoCrafterScreen::new);
        ScreenRegistry.register(ModScreenHandlers.ITEM_ACTION_SCREEN_HANDLER, ItemActionScreen::new);
        ScreenRegistry.register(ModScreenHandlers.BLOCK_MINER_SCREEN_HANDLER, BlockMinerScreen::new);
        ScreenRegistry.register(ModScreenHandlers.BLOCK_PLACER_SCREEN_HANDLER, BlockPlacerScreen::new);
        ScreenRegistry.register(ModScreenHandlers.GROWTH_ACCELERATOR_SCREEN_HANDLER, GrowthAcceleratorScreen::new);
        ScreenRegistry.register(ModScreenHandlers.ALCHEMY_TABLE_SCREEN_HANDLER, AlchemyTableScreen::new);
        ScreenRegistry.register(ModScreenHandlers.GRAND_SHULKER_BOX_SCREEN_HANDLER, net.willowins.animewitchery.screen.GrandShulkerBoxScreen::new);

        new DebugScreenInterceptor().onInitializeClient();

        ModPackets.registerS2CPackets();

        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.OBELISK.asItem(), new ObeliskBuiltinItemRenderer());

        VoidBoundShaderRenderer.cleanup();
        VoidBoundShaderRenderer.init();
        ModelPredicateProviderRegistry.register(
                Items.CROSSBOW,
                new Identifier("animewitchery", "mana_charged"),
                (stack, world, entity, seed) -> {
                    if (!CrossbowItem.isCharged(stack)) return 0f;
                    NbtCompound nbt = stack.getNbt();
                    if (nbt == null || !nbt.contains("ChargedProjectiles", 9)) return 0f;
                    NbtList list = nbt.getList("ChargedProjectiles", 10);
                    for (int i = 0; i < list.size(); i++) {
                        ItemStack proj = ItemStack.fromNbt(list.getCompound(i));
                        if (proj.getItem() instanceof ManaRocketItem) return 1f;
                    }
                    return 0f;
                }
        );
        ModelPredicateProviderRegistry.register(ModItems.OBELISK_COMPASS,
                new Identifier("angle"),
                (stack, world, entity, seed) -> {
                    if (stack.hasNbt()) {
                        float angle = stack.getNbt().getFloat("ObeliskCompassAngle");
                        return angle / 360f;
                    }
                    return 0f;
                });


        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                        registrationHelper.register(new FlightElytraRenderLayer(playerRenderer));
                    }
                }
        );

        EntityRendererRegistry.register(
                ModEntities.KINETIC_BLADE_HITBOX,
                (context) -> new EmptyEntityRenderer<>(context)
        );

    }

    }


