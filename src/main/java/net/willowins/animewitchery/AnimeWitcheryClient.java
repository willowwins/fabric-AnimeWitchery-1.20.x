package net.willowins.animewitchery;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
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
import net.willowins.animewitchery.fluid.ModFluids;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.renderer.*;
import net.willowins.animewitchery.block.entity.renderer.GrandShulkerBoxRenderer;
import net.willowins.animewitchery.entity.client.render.MonsterStatueRenderer;
import net.willowins.animewitchery.client.DebugScreenInterceptor;
import net.willowins.animewitchery.client.render.FlightElytraRenderLayer;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.entity.client.render.NeedleProjectileRenderer;
import net.willowins.animewitchery.entity.client.render.VoidWispRenderer;
import net.willowins.animewitchery.entity.client.KamikazeRitualRenderer;
import net.willowins.animewitchery.entity.client.ResonantShieldEntityRenderer;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.ManaRocketItem;

import net.willowins.animewitchery.networking.ModPackets;
import net.willowins.animewitchery.particle.ModParticles;
import net.willowins.animewitchery.particle.ShockwaveParticle;
import net.willowins.animewitchery.screen.*;
import net.willowins.animewitchery.client.sky.SkyRitualRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.willowins.animewitchery.item.renderer.ObeliskBuiltinItemRenderer;
import net.willowins.animewitchery.item.renderer.RailgunRenderer;
import net.willowins.animewitchery.item.renderer.HealingStaffRenderer;
import net.willowins.animewitchery.item.renderer.ObeliskSwordRenderer;
import net.willowins.animewitchery.client.shader.VoidBoundPostProcessor;

public class AnimeWitcheryClient implements ClientModInitializer {
        public static boolean IS_RENDERING_ARMOR = false;

        @Override
        public void onInitializeClient() {
                // Register Dimension Effects for Paradise Lost
                net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.registerDimensionEffects(
                                new net.minecraft.util.Identifier(AnimeWitchery.MOD_ID, "paradiselost"),
                                new net.willowins.animewitchery.client.render.ParadiseLostDimensionEffects());

                net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry.registerSkyRenderer(
                                net.willowins.animewitchery.world.dimension.ModDimensions.PARADISELOSTDIM_LEVEL_KEY,
                                new net.willowins.animewitchery.client.render.ParadiseLostSkyRenderer());

                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SILVER_DOOR, RenderLayer.getCutout());

                FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_STARLIGHT, ModFluids.FLOWING_STARLIGHT,
                                new SimpleFluidRenderHandler(
                                                new Identifier("minecraft:block/water_still"),
                                                new Identifier("minecraft:block/water_flow"),
                                                0xFFF5FFFF) {
                                        @Override
                                        public int getFluidColor(net.minecraft.world.BlockRenderView view,
                                                        net.minecraft.util.math.BlockPos pos,
                                                        net.minecraft.fluid.FluidState state) {
                                                return 0xFFF5FFFF;
                                        }
                                });

                BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.STILL_STARLIGHT,
                                ModFluids.FLOWING_STARLIGHT);
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
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TRANSMUTATION_PYRE_BLOCK, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GACHA_ALTAR, RenderLayer.getCutout());

                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STRAWBERRY_CROP, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LEMON_CROP, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FLOAT_BLOCK, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AUTO_CRAFTER_BLOCK, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROSEWILLOW_VINES, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROSEWILLOW_VINES_TIP, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.LARGE_ROSEWILLOW_BLOSSOM, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROSEWILLOW_LEAVES, RenderLayer.getCutout());
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROSEWILLOW_SAPLING, RenderLayer.getCutout());

                BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_EFFIGY_FOUNTAIN_BLOCK_ENTITY,
                                (context) -> new ActiveEffigyFountainRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY,
                                (context) -> new ActiveBindingSpellRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.BINDING_SPELL_BLOCK_ENTITY,
                                (context) -> new BindingSpellRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY,
                                AlchemyTableRenderer::new);
                BlockEntityRendererRegistry.register(ModBlockEntities.OBELISK_BLOCK_ENTITY,
                                (context) -> new ObeliskRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY,
                                (context) -> new ActiveObeliskRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY,
                                (context) -> new BarrierCircleRenderer(context));
                BlockEntityRendererRegistry.register(ModBlockEntities.BARRIER_DISTANCE_GLYPH_BLOCK_ENTITY,
                                (context) -> new BarrierDistanceGlyphRenderer(context));
                BlockEntityRendererRegistry.register(ModBlockEntities.BOSS_OBELISK_BLOCK_ENTITY,
                                (context) -> new BossObeliskRenderer());
                BlockEntityRendererRegistry.register(ModBlockEntities.GUARDIAN_STATUE_BLOCK_ENTITY,
                                GuardianStatueRenderer::new);
                BlockEntityRendererRegistry.register(ModBlockEntities.PLATE_BLOCK_ENTITY, PlateRenderer::new);
                BlockEntityRendererRegistry.register(ModBlockEntities.TRANSMUTATION_PYRE_BLOCK_ENTITY,
                                TransmutationPyreRenderer::new);
                BlockEntityRendererRegistry.register(ModBlockEntities.DEEPSLATE_THRESHOLD_ENTITY,
                                (context) -> new DeepslateThresholdRendererFixed());
                // Register Grand Shulker Box GeckoLib renderer
                BlockEntityRendererRegistry.register(ModBlocks.GRAND_SHULKER_BOX_ENTITY, GrandShulkerBoxRenderer::new);

                // Register Protected Chest Renderer (Standard Chest)
                BlockEntityRendererRegistry.register(ModBlocks.PROTECTED_CHEST_ENTITY,
                                net.minecraft.client.render.block.entity.ChestBlockEntityRenderer::new);

                EntityRendererRegistry.register(ModEntities.NEEDLE_PROJECTILE, NeedleProjectileRenderer::new);
                EntityRendererRegistry.register(ModEntities.VOID_WISP, VoidWispRenderer::new);
                EntityRendererRegistry.register(ModEntities.KAMIKAZE_RITUAL, KamikazeRitualRenderer::new);
                EntityRendererRegistry.register(ModEntities.RESONANT_SHIELD, ResonantShieldEntityRenderer::new);
                EntityRendererRegistry.register(ModEntities.RESONANT_SPARK,
                                (context) -> new net.minecraft.client.render.entity.FlyingItemEntityRenderer<net.willowins.animewitchery.entity.custom.ResonantSparkEntity>(
                                                context));

                // Monster Statue
                BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MONSTER_STATUE, RenderLayer.getCutout());
                BlockEntityRendererRegistry.register(ModBlockEntities.MONSTER_STATUE_BLOCK_ENTITY,
                                MonsterStatueRenderer::new);

                ModelPredicateProviderRegistry.register(ModItems.WEATHERITEM,
                                new Identifier(AnimeWitchery.MOD_ID, "mode"),
                                (stack, world, entity, seed) -> {
                                        return net.willowins.animewitchery.item.custom.WeatherItem.getMode(stack);
                                });

                ParticleFactoryRegistry.getInstance().register(ModParticles.LASER_PARTICLE,
                                ShockwaveParticle.Factory::new);
                ScreenRegistry.register(ModScreenHandlers.PLAYER_USE_DISPENSER_SCREEN_HANDLER,
                                PlayerUseDispenserScreen::new);
                ScreenRegistry.register(ModScreenHandlers.AUTO_CRAFTER_SCREEN_HANDLER, AutoCrafterScreen::new);
                ScreenRegistry.register(ModScreenHandlers.ITEM_ACTION_SCREEN_HANDLER, ItemActionScreen::new);
                ScreenRegistry.register(ModScreenHandlers.BLOCK_MINER_SCREEN_HANDLER, BlockMinerScreen::new);
                ScreenRegistry.register(ModScreenHandlers.BLOCK_PLACER_SCREEN_HANDLER, BlockPlacerScreen::new);
                ScreenRegistry.register(ModScreenHandlers.GROWTH_ACCELERATOR_SCREEN_HANDLER,
                                GrowthAcceleratorScreen::new);
                ScreenRegistry.register(ModScreenHandlers.ALCHEMY_TABLE_SCREEN_HANDLER, AlchemyTableScreen::new);
                ScreenRegistry.register(ModScreenHandlers.GRAND_SHULKER_BOX_SCREEN_HANDLER,
                                net.willowins.animewitchery.screen.GrandShulkerBoxScreen::new);
                ScreenRegistry.register(ModScreenHandlers.ADVANCED_SPELLBOOK_SCREEN_HANDLER,
                                net.willowins.animewitchery.screen.AdvancedSpellbookScreen::new);
                ScreenRegistry.register(ModScreenHandlers.SOUL_JAR_SCREEN_HANDLER, SoulJarScreen::new);
                ScreenRegistry.register(ModScreenHandlers.COSMETIC_BAG_HANDLER, CosmeticBagScreen::new);
                ScreenRegistry.register(ModScreenHandlers.ALCHEMICAL_ENCHANTER_SCREEN_HANDLER,
                                AlchemicalEnchanterScreen::new);

                new DebugScreenInterceptor().onInitializeClient();

                ModPackets.registerS2CPackets();
                new net.willowins.animewitchery.client.ManaHudOverlay().onInitializeClient();

                // Manual Registration of GeckoLib Renderers
                System.out.println("ANIMEWITCHERY: Registering Manual Renderers [DEBUG]");
                BuiltinItemRendererRegistry.INSTANCE.register(ModItems.RAILGUN, new RailgunRenderer()::render);
                BuiltinItemRendererRegistry.INSTANCE.register(ModItems.HEALING_STAFF,
                                new HealingStaffRenderer()::render);
                BuiltinItemRendererRegistry.INSTANCE.register(ModItems.OBELISK_SWORD,
                                new ObeliskSwordRenderer()::render);

                BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.OBELISK.asItem(),
                                new ObeliskBuiltinItemRenderer());

                VoidBoundPostProcessor.cleanup();
                VoidBoundPostProcessor.init();
                ModelPredicateProviderRegistry.register(
                                Items.CROSSBOW,
                                new Identifier("animewitchery", "mana_charged"),
                                (stack, world, entity, seed) -> {
                                        if (!CrossbowItem.isCharged(stack))
                                                return 0f;
                                        NbtCompound nbt = stack.getNbt();
                                        if (nbt == null || !nbt.contains("ChargedProjectiles", 9))
                                                return 0f;
                                        NbtList list = nbt.getList("ChargedProjectiles", 10);
                                        for (int i = 0; i < list.size(); i++) {
                                                ItemStack proj = ItemStack.fromNbt(list.getCompound(i));
                                                if (proj.getItem() instanceof ManaRocketItem)
                                                        return 1f;
                                        }
                                        return 0f;
                                });

                ModelPredicateProviderRegistry.register(
                                ModItems.OATHBREAKER,
                                new Identifier("animewitchery", "active_state"),
                                (stack, world, entity, seed) -> {
                                        return net.willowins.animewitchery.item.custom.OathbreakerItem.isActive(stack)
                                                        ? 1.0f
                                                        : 0.0f;
                                });

                LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                                (entityType, entityRenderer, registrationHelper, context) -> {
                                        if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {
                                                registrationHelper
                                                                .register(new FlightElytraRenderLayer(playerRenderer));
                                        }
                                });

                EntityRendererRegistry.register(
                                ModEntities.KINETIC_BLADE_HITBOX,
                                (context) -> new EmptyEntityRenderer<>(context));

                net.willowins.animewitchery.client.DiviningRodRenderer.register();

                net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AFTER_TRANSLUCENT
                                .register(net.willowins.animewitchery.client.PocketGridRenderer::render);

                net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                        return tintIndex == 0 ? net.minecraft.potion.PotionUtil.getColor(stack) : -1;
                }, ModItems.POTION_FLASK);

                // Rosewillow Leaves tinting (matches dark oak leaves)
                net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register(
                                (state, world, pos, tintIndex) -> {
                                        return world != null && pos != null
                                                        ? net.minecraft.client.color.world.BiomeColors
                                                                        .getFoliageColor(world, pos)
                                                        : 0x48B518; // Default foliage color
                                }, ModBlocks.ROSEWILLOW_LEAVES);

                net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register(
                                (stack, tintIndex) -> {
                                        return 0x48B518; // Default foliage color for items
                                }, ModBlocks.ROSEWILLOW_LEAVES);

                // Class System Keybinding
                net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(KEY_CLASS_MENU);
                net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
                        while (KEY_CLASS_MENU.wasPressed()) {
                                if (client.player != null) {
                                        net.willowins.animewitchery.component.IClassComponent classData = net.willowins.animewitchery.mana.ModComponents.CLASS_DATA
                                                        .get(client.player);

                                        if (classData.getPrimaryClass().isEmpty()) {
                                                client.setScreen(
                                                                new net.willowins.animewitchery.screen.class_system.ClassSelectionScreen(
                                                                                false));
                                        } else if (classData.getLevel() >= 100 && !classData.hasSecondaryClass()) {
                                                client.setScreen(
                                                                new net.willowins.animewitchery.screen.class_system.ClassSelectionScreen(
                                                                                true));
                                        } else {
                                                client.setScreen(
                                                                new net.willowins.animewitchery.screen.class_system.SkillTreeScreen(
                                                                                classData));
                                        }
                                }
                        }
                });
        }

        public static final net.minecraft.client.option.KeyBinding KEY_CLASS_MENU = new net.minecraft.client.option.KeyBinding(
                        "key.animewitchery.class_menu",
                        org.lwjgl.glfw.GLFW.GLFW_KEY_KP_9,
                        "category.animewitchery.general");

        @SuppressWarnings("unchecked")
        private static <T extends Comparable<T>> String nameValue(net.minecraft.state.property.Property<T> property,
                        Comparable<?> value) {
                return property.getName() + "=" + property.name((T) value);
        }

}
