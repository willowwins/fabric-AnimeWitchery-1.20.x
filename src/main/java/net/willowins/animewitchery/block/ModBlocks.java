package net.willowins.animewitchery.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.custom.*;
import net.willowins.animewitchery.block.custom.PillarBlock;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;

import java.util.Objects;

import net.willowins.animewitchery.fluid.ModFluids;
import net.willowins.animewitchery.block.custom.StarlightFluidBlock;
import net.minecraft.block.FluidBlock;

public class ModBlocks {
        public static final Block STARLIGHT_BLOCK = registerBlockWithoutItem("starlight_block",
                        new StarlightFluidBlock(ModFluids.STILL_STARLIGHT,
                                        FabricBlockSettings.copy(Blocks.WATER)
                                                        .mapColor(net.minecraft.block.MapColor.WATER_BLUE)
                                                        .luminance((state) -> 15).noCollision()));

        /*
         * ----------------------------------------------------------
         * 🟪 GRAND SHULKER BOX (DEFERRED REGISTRATION)
         * ----------------------------------------------------------
         */
        public static Block GRAND_SHULKER_BOX;
        public static BlockEntityType<GrandShulkerBoxBlockEntity> GRAND_SHULKER_BOX_ENTITY;

        /*
         * ----------------------------------------------------------
         * 🔒 PROTECTED CHEST (DEFERRED REGISTRATION)
         * ----------------------------------------------------------
         */
        public static Block PROTECTED_CHEST;
        public static BlockEntityType<net.willowins.animewitchery.block.entity.ProtectedChestBlockEntity> PROTECTED_CHEST_ENTITY;

        /*
         * ----------------------------------------------------------
         * ⚙️ OTHER BLOCKS
         * ----------------------------------------------------------
         */
        public static final Block AUTO_CRAFTER_BLOCK = registerBlock("auto_crafter",
                        new AutoCrafterBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block BLOCK_MINER = registerBlock("block_miner",
                        new BlockMinerBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)));

        public static final Block GROWTH_ACCELERATOR = registerBlock("growth_accelerator",
                        new GrowthAcceleratorBlock(FabricBlockSettings.copyOf(Blocks.DISPENSER)));

        public static final Block BLOCK_PLACER = registerBlock("block_placer",
                        new BlockPlacerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block INTERACTOR = registerBlock("interactor",
                        new ItemActionBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static Block WORLD_BUTTON = registerBlock("world_button",
                        new WorldButtonBlock(FabricBlockSettings.copyOf(Blocks.STONE_BUTTON).nonOpaque()));

        public static Block SERVER_BUTTON = registerBlock("server_button",
                        new ServerButtonBlock(FabricBlockSettings.copyOf(Blocks.STONE_BUTTON).nonOpaque()));

        public static final Block SILVER_BLOCK = registerBlock("silver_block",
                        new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block PARTICLE_BLOCK = registerBlock("particle_block",
                        new ParticleBeamBlock());

        public static final Block PARTICLE_SINK_BLOCK = registerBlock("particle_sink_block",
                        new ParticleSinkBlock());

        public static final Block EFFIGY_FOUNTAIN = registerBlock("effigy_fountain",
                        new EffigyFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block DECORATIVE_FOUNTAIN = registerBlock("decorative_fountain",
                        new DecorativeFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block BINDING_SPELL = registerBlock("binding_spell",
                        new BindingSpellBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

        public static final Block ALCHEMY_TABLE = registerBlock("alchemy_table",
                        new AlchemyTableBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block ALCHEMICAL_ENCHANTER = registerBlock("alchemical_enchanter",
                        new AlchemicalEnchanterBlock(FabricBlockSettings.copyOf(Blocks.ENCHANTING_TABLE).nonOpaque()));

        public static final Block GACHA_ALTAR = registerBlock("gacha_altar",
                        new GachaAltarBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block PLATE_BLOCK = registerBlock("plate",
                        new PlateBlock(FabricBlockSettings.copyOf(SILVER_BLOCK).nonOpaque()));

        public static final Block ACTIVE_EFFIGY_FOUNTAIN = registerBlock("active_effigy_fountain",
                        new ActiveEffigyFountainBlock(
                                        FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

        public static final Block ACTIVE_BINDING_SPELL = registerBlock("active_binding_spell",
                        new ActiveBindingSpellBlock(
                                        FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

        public static final Block OBELISK = registerBlock("obelisk",
                        new ObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block ACTIVE_OBELISK = registerBlock("active_obelisk",
                        new ActiveObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

        public static final Block BOSS_OBELISK = registerBlock("boss_obelisk",
                        new BossObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

        public static final Block BARRIER_CIRCLE = registerBlock("barrier_circle",
                        new BarrierCircleBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block BARRIER_DISTANCE_GLYPH = registerBlock("barrier_distance_glyph",
                        new BarrierDistanceGlyphBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block GUARDIAN_STATUE = registerBlock("guardian_statue",
                        new GuardianStatueBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block PILLAR = registerBlock("pillar",
                        new PillarBlock(FabricBlockSettings.copyOf(Blocks.STONE)));

        public static final Block SOUND_BLOCK = registerBlock("sound_block",
                        new SoundBlock(FabricBlockSettings.copyOf(Blocks.JUKEBOX)));

        public static final Block SOUND_BLOCK2 = registerBlock("sound_block2",
                        new SoundBlock2(FabricBlockSettings.copyOf(Blocks.JUKEBOX)));

        public static final Block SILVER_STAIRS = registerBlock("silver_stairs",
                        new StairsBlock(SILVER_BLOCK.getDefaultState(), FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block SILVER_SLAB = registerBlock("silver_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block SILVER_BUTTON = registerBlock("silver_button",
                        new ButtonBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), BlockSetType.IRON, 30, true));

        public static final Block SILVER_PRESSURE_PLATE = registerBlock("silver_pressure_plate",
                        new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                                        FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), BlockSetType.IRON));

        public static final Block SILVER_FENCE = registerBlock("silver_fence",
                        new FenceBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block SILVER_FENCE_GATE = registerBlock("silver_fence_gate",
                        new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), WoodType.DARK_OAK));

        public static final Block SILVER_WALL = registerBlock("silver_wall",
                        new WallBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block SILVER_DOOR = registerBlock("silver_door",
                        new DoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_DOOR), BlockSetType.IRON));

        public static final Block SILVER_TRAPDOOR = registerBlock("silver_trapdoor",
                        new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_TRAPDOOR), BlockSetType.IRON));

        public static final Block SILVER_ORE = registerBlock("silver_ore",
                        new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.STONE).strength(1.5f),
                                        UniformIntProvider.create(5, 7)));

        public static final Block SHIMMERING_FARMLAND = registerBlock("shimmering_farmland",
                        new ShimmeringFarmlandBlock(FabricBlockSettings.copyOf(Blocks.FARMLAND).luminance(state -> 7)));

        public static final Block CHARCOAL_BLOCK = registerBlock("charcoal_block",
                        new Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK)));

        public static final Block DEEPSLATE_SILVER_ORE = registerBlock("deepslate_silver_ore",
                        new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE),
                                        UniformIntProvider.create(7, 10)));

        public static final Block STRAWBERRY_CROP = Registry.register(
                        Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "strawberry_crop"),
                        new StrawberryCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));

        public static final Block LEMON_CROP = Registry.register(
                        Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "lemon_crop"),
                        new LemonCropBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_SAPLING)));

        public static final Block FLOAT_BLOCK = registerBlock("float_block",
                        new FloatBlock(FabricBlockSettings.copyOf(Blocks.BARRIER)));

        public static final Block SPELL_TRIGGER_BLOCK = registerBlock("spell_trigger_block",
                        new net.willowins.animewitchery.block.custom.SpellTriggerBlock(
                                        FabricBlockSettings.copyOf(Blocks.STONE).strength(2.0f)));

        public static final Block TRANSMUTATION_PYRE_BLOCK = registerBlock("transmutation_pyre_block",
                        new TransmutationPyreBlock(FabricBlockSettings.create().strength(2.0f).nonOpaque()
                                        .luminance(state -> 10)));

        public static final Block CAUTION_BLOCK = registerBlock("caution_block",
                        new Block(FabricBlockSettings.create().strength(2.0f)));

        public static final Block CAUTION_BLOCK_SLAB = registerBlock("caution_block_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(CAUTION_BLOCK)));

        public static final Block CAUTION_BLOCK_STAIRS = registerBlock("caution_block_stairs",
                        new StairsBlock(CAUTION_BLOCK.getDefaultState(), FabricBlockSettings.copyOf(CAUTION_BLOCK)));

        public static final Block IRON_STAIRS = registerBlock("iron_stairs",
                        new StairsBlock(Blocks.IRON_BLOCK.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
        public static final Block IRON_SLAB = registerBlock("iron_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        public static final Block COPPER_STAIRS = registerBlock("copper_stairs",
                        new StairsBlock(Blocks.COPPER_BLOCK.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)));
        public static final Block COPPER_SLAB = registerBlock("copper_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)));

        public static final Block GOLD_STAIRS = registerBlock("gold_stairs",
                        new StairsBlock(Blocks.GOLD_BLOCK.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));
        public static final Block GOLD_SLAB = registerBlock("gold_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK)));

        public static final Block DIAMOND_STAIRS = registerBlock("diamond_stairs",
                        new StairsBlock(Blocks.DIAMOND_BLOCK.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK)));
        public static final Block DIAMOND_SLAB = registerBlock("diamond_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK)));

        public static final Block NETHERITE_STAIRS = registerBlock("netherite_stairs",
                        new StairsBlock(Blocks.NETHERITE_BLOCK.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK)));
        public static final Block NETHERITE_SLAB = registerBlock("netherite_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.NETHERITE_BLOCK)));

        // Deepslate Threshold
        public static final Block MONSTER_STATUE = registerBlockWithoutItem("monster_statue",
                        new MonsterStatueBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

        public static final Block DEEPSLATE_THRESHOLD = registerBlock("deepslate_threshold",
                        new DeepslateThresholdBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE)));

        // Concrete Stairs and Slabs
        public static final Block WHITE_CONCRETE_STAIRS = registerBlock("white_concrete_stairs",
                        new StairsBlock(Blocks.WHITE_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)));
        public static final Block WHITE_CONCRETE_SLAB = registerBlock("white_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.WHITE_CONCRETE)));

        public static final Block ORANGE_CONCRETE_STAIRS = registerBlock("orange_concrete_stairs",
                        new StairsBlock(Blocks.ORANGE_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.ORANGE_CONCRETE)));
        public static final Block ORANGE_CONCRETE_SLAB = registerBlock("orange_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.ORANGE_CONCRETE)));

        public static final Block MAGENTA_CONCRETE_STAIRS = registerBlock("magenta_concrete_stairs",
                        new StairsBlock(Blocks.MAGENTA_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.MAGENTA_CONCRETE)));
        public static final Block MAGENTA_CONCRETE_SLAB = registerBlock("magenta_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.MAGENTA_CONCRETE)));

        public static final Block LIGHT_BLUE_CONCRETE_STAIRS = registerBlock("light_blue_concrete_stairs",
                        new StairsBlock(Blocks.LIGHT_BLUE_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.LIGHT_BLUE_CONCRETE)));
        public static final Block LIGHT_BLUE_CONCRETE_SLAB = registerBlock("light_blue_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.LIGHT_BLUE_CONCRETE)));

        public static final Block YELLOW_CONCRETE_STAIRS = registerBlock("yellow_concrete_stairs",
                        new StairsBlock(Blocks.YELLOW_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.YELLOW_CONCRETE)));
        public static final Block YELLOW_CONCRETE_SLAB = registerBlock("yellow_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.YELLOW_CONCRETE)));

        public static final Block LIME_CONCRETE_STAIRS = registerBlock("lime_concrete_stairs",
                        new StairsBlock(Blocks.LIME_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.LIME_CONCRETE)));
        public static final Block LIME_CONCRETE_SLAB = registerBlock("lime_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.LIME_CONCRETE)));

        public static final Block PINK_CONCRETE_STAIRS = registerBlock("pink_concrete_stairs",
                        new StairsBlock(Blocks.PINK_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.PINK_CONCRETE)));
        public static final Block PINK_CONCRETE_SLAB = registerBlock("pink_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.PINK_CONCRETE)));

        public static final Block GRAY_CONCRETE_STAIRS = registerBlock("gray_concrete_stairs",
                        new StairsBlock(Blocks.GRAY_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.GRAY_CONCRETE)));
        public static final Block GRAY_CONCRETE_SLAB = registerBlock("gray_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.GRAY_CONCRETE)));

        public static final Block LIGHT_GRAY_CONCRETE_STAIRS = registerBlock("light_gray_concrete_stairs",
                        new StairsBlock(Blocks.LIGHT_GRAY_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.LIGHT_GRAY_CONCRETE)));
        public static final Block LIGHT_GRAY_CONCRETE_SLAB = registerBlock("light_gray_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.LIGHT_GRAY_CONCRETE)));

        public static final Block CYAN_CONCRETE_STAIRS = registerBlock("cyan_concrete_stairs",
                        new StairsBlock(Blocks.CYAN_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.CYAN_CONCRETE)));
        public static final Block CYAN_CONCRETE_SLAB = registerBlock("cyan_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.CYAN_CONCRETE)));

        public static final Block PURPLE_CONCRETE_STAIRS = registerBlock("purple_concrete_stairs",
                        new StairsBlock(Blocks.PURPLE_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.PURPLE_CONCRETE)));
        public static final Block PURPLE_CONCRETE_SLAB = registerBlock("purple_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.PURPLE_CONCRETE)));

        public static final Block BLUE_CONCRETE_STAIRS = registerBlock("blue_concrete_stairs",
                        new StairsBlock(Blocks.BLUE_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.BLUE_CONCRETE)));
        public static final Block BLUE_CONCRETE_SLAB = registerBlock("blue_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.BLUE_CONCRETE)));

        public static final Block BROWN_CONCRETE_STAIRS = registerBlock("brown_concrete_stairs",
                        new StairsBlock(Blocks.BROWN_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.BROWN_CONCRETE)));
        public static final Block BROWN_CONCRETE_SLAB = registerBlock("brown_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.BROWN_CONCRETE)));

        public static final Block GREEN_CONCRETE_STAIRS = registerBlock("green_concrete_stairs",
                        new StairsBlock(Blocks.GREEN_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.GREEN_CONCRETE)));
        public static final Block GREEN_CONCRETE_SLAB = registerBlock("green_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.GREEN_CONCRETE)));

        public static final Block RED_CONCRETE_STAIRS = registerBlock("red_concrete_stairs",
                        new StairsBlock(Blocks.RED_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.RED_CONCRETE)));
        public static final Block RED_CONCRETE_SLAB = registerBlock("red_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.RED_CONCRETE)));

        public static final Block BLACK_CONCRETE_STAIRS = registerBlock("black_concrete_stairs",
                        new StairsBlock(Blocks.BLACK_CONCRETE.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.BLACK_CONCRETE)));
        public static final Block BLACK_CONCRETE_SLAB = registerBlock("black_concrete_slab",
                        new SlabBlock(FabricBlockSettings.copyOf(Blocks.BLACK_CONCRETE)));

        // Rosewillow Wood Set
        public static final Block ROSEWILLOW_LOG = registerBlock("rosewillow_log",
                        new net.minecraft.block.PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(2.0f)));

        public static final Block ROSEWILLOW_LOG_BLOOMING = registerBlock("rosewillow_log_blooming",
                        new net.minecraft.block.PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).strength(2.0f)));

        public static final Block ROSEWILLOW_SAPLING = registerBlock("rosewillow_sapling",
                        new RosewillowSaplingBlock(
                                        new net.willowins.animewitchery.world.gen.RosewillowSaplingGenerator(),
                                        FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)));

        public static final Block ROSEWILLOW_PLANKS = registerBlock("rosewillow_planks",
                        new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0f)));

        public static final Block ROSEWILLOW_STAIRS = registerBlock("rosewillow_stairs",
                        new RosewillowStairsBlock(ROSEWILLOW_PLANKS.getDefaultState(),
                                        FabricBlockSettings.copyOf(Blocks.OAK_STAIRS)));

        public static final Block ROSEWILLOW_SLAB = registerBlock("rosewillow_slab",
                        new RosewillowSlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_SLAB)));

        public static final Block ROSEWILLOW_FENCE = registerBlock("rosewillow_fence",
                        new RosewillowFenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE)));

        public static final Block ROSEWILLOW_FENCE_GATE = registerBlock("rosewillow_fence_gate",
                        new RosewillowFenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_FENCE_GATE), WoodType.OAK));

        public static final Block LARGE_ROSEWILLOW_BLOSSOM = registerBlock("large_rosewillow_blossom",
                        new LargeRosewillowBlossomBlock(FabricBlockSettings.copyOf(Blocks.SPORE_BLOSSOM)));

        public static final Block ROSEWILLOW_LEAVES = registerBlock("rosewillow_leaves",
                        new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES)));

        public static final Block ROSEWILLOW_VINES = Registry.register(Registries.BLOCK,
                        new Identifier(AnimeWitchery.MOD_ID, "rosewillow_vines"),
                        new RoseWillowVineBodyBlock(FabricBlockSettings.copyOf(Blocks.CAVE_VINES_PLANT)));

        public static final Block ROSEWILLOW_VINES_TIP = Registry.register(Registries.BLOCK,
                        new Identifier(AnimeWitchery.MOD_ID, "rosewillow_vines_tip"),
                        new RoseWillowVineHeadBlock(FabricBlockSettings.copyOf(Blocks.CAVE_VINES)));

        public static final Block ROSEWILLOW_ROOTS = registerBlock("rosewillow_roots",
                        new RosewillowRootsBlock(FabricBlockSettings.copyOf(Blocks.ROOTED_DIRT).ticksRandomly()));

        public static final Block ROSEWILLOW_BULB = registerBlock("rosewillow_bulb",
                        new RosewillowBulbBlock(FabricBlockSettings.copyOf(Blocks.SHROOMLIGHT).ticksRandomly()
                                        .luminance(10)));

        /*
         * ----------------------------------------------------------
         * 🧩 REGISTRATION LOGIC
         * ----------------------------------------------------------
         */
        public static void registerBlocks() {
                // 🟣 Create & register Grand Shulker Box
                GRAND_SHULKER_BOX = new GrandShulkerBoxBlock(
                                net.minecraft.util.DyeColor.PURPLE,
                                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque());

                // Register Grand Shulker Box
                Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box"),
                                GRAND_SHULKER_BOX);

                // Register Grand Shulker Box item
                Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box"),
                                new net.willowins.animewitchery.item.custom.GrandShulkerBoxItem(GRAND_SHULKER_BOX,
                                                new FabricItemSettings().maxCount(8)));

                // Register block entity type
                GRAND_SHULKER_BOX_ENTITY = Registry.register(
                                Registries.BLOCK_ENTITY_TYPE,
                                new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_entity"),
                                FabricBlockEntityTypeBuilder.create(
                                                GrandShulkerBoxBlockEntity::new,
                                                GRAND_SHULKER_BOX).build());

                AnimeWitchery.LOGGER.info("✅ Registered Grand Shulker Box successfully.");

                // 🔒 Create & register Protected Chest
                PROTECTED_CHEST = new ProtectedChestBlock(
                                FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(-1.0f, 3600000.0f));

                // Register Protected Chest
                Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "protected_chest"),
                                PROTECTED_CHEST);

                // Register Protected Chest item
                Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, "protected_chest"),
                                new BlockItem(PROTECTED_CHEST, new FabricItemSettings()));

                AnimeWitchery.LOGGER.info("✅ Registered Protected Chest successfully.");

                // Register Protected Chest Block Entity
                PROTECTED_CHEST_ENTITY = Registry.register(
                                Registries.BLOCK_ENTITY_TYPE,
                                new Identifier(AnimeWitchery.MOD_ID, "protected_chest_entity"),
                                FabricBlockEntityTypeBuilder.create(
                                                net.willowins.animewitchery.block.entity.ProtectedChestBlockEntity::new,
                                                PROTECTED_CHEST).build());
        }

        /*
         * ----------------------------------------------------------
         * ⚙️ HELPERS
         * ----------------------------------------------------------
         */
        public static final Block LANDING_PLATFORM = registerBlock("landing_platform",
                        new net.willowins.animewitchery.block.custom.LandingPlatformBlock(
                                        FabricBlockSettings.copyOf(Blocks.BEDROCK)));
        public static final Block ENEMY_LANDING_PLATFORM = registerBlock("enemy_landing_platform",
                        new net.willowins.animewitchery.block.custom.EnemyLandingPlatformBlock(
                                        FabricBlockSettings.copyOf(Blocks.BEDROCK)));

        private static Block registerBlock(String name, Block block) {
                if (!Objects.equals(name, "float_block")) {
                        registerBlockItem(name, block);
                }
                return Registry.register(Registries.BLOCK,
                                new Identifier(AnimeWitchery.MOD_ID, name), block);
        }

        private static Item registerBlockItem(String name, Block block) {
                return Registry.register(Registries.ITEM,
                                new Identifier(AnimeWitchery.MOD_ID, name),
                                new BlockItem(block, new FabricItemSettings()));
        }

        private static Block registerBlockWithoutItem(String name, Block block) {
                return Registry.register(Registries.BLOCK,
                                new Identifier(AnimeWitchery.MOD_ID, name), block);
        }

        // Static initializer to register custom block items
        static {
                // Monster Statue with tooltip
                Registry.register(Registries.ITEM,
                                new Identifier(AnimeWitchery.MOD_ID, "monster_statue"),
                                new net.willowins.animewitchery.item.custom.MonsterStatueBlockItem(MONSTER_STATUE,
                                                new FabricItemSettings()));
        }

        public static void registerModBlocks() {
                AnimeWitchery.LOGGER.info("Registering ModBlocks for " + AnimeWitchery.MOD_ID);
                registerBlocks(); // Call the Grand Shulker Box registration
        }
}
