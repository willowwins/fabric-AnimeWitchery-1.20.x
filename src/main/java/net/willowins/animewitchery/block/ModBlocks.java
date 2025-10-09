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

public class ModBlocks {

    /* ----------------------------------------------------------
       🟪 GRAND SHULKER BOX (DEFERRED REGISTRATION)
       ---------------------------------------------------------- */
    public static Block GRAND_SHULKER_BOX;
    public static Block GRAND_SHULKER_BOX_WHITE;
    public static Block GRAND_SHULKER_BOX_ORANGE;
    public static Block GRAND_SHULKER_BOX_MAGENTA;
    public static Block GRAND_SHULKER_BOX_LIGHT_BLUE;
    public static Block GRAND_SHULKER_BOX_YELLOW;
    public static Block GRAND_SHULKER_BOX_LIME;
    public static Block GRAND_SHULKER_BOX_PINK;
    public static Block GRAND_SHULKER_BOX_GRAY;
    public static Block GRAND_SHULKER_BOX_LIGHT_GRAY;
    public static Block GRAND_SHULKER_BOX_CYAN;
    public static Block GRAND_SHULKER_BOX_PURPLE;
    public static Block GRAND_SHULKER_BOX_BLUE;
    public static Block GRAND_SHULKER_BOX_BROWN;
    public static Block GRAND_SHULKER_BOX_GREEN;
    public static Block GRAND_SHULKER_BOX_RED;
    public static Block GRAND_SHULKER_BOX_BLACK;
    public static BlockEntityType<GrandShulkerBoxBlockEntity> GRAND_SHULKER_BOX_ENTITY;

    /* ----------------------------------------------------------
       ⚙️ OTHER BLOCKS
       ---------------------------------------------------------- */
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

    public static final Block PLATE_BLOCK = registerBlock("plate",
            new PlateBlock(FabricBlockSettings.copyOf(SILVER_BLOCK).nonOpaque()));

    public static final Block ACTIVE_EFFIGY_FOUNTAIN = registerBlock("active_effigy_fountain",
            new ActiveEffigyFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

    public static final Block ACTIVE_BINDING_SPELL = registerBlock("active_binding_spell",
            new ActiveBindingSpellBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

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

    /* ----------------------------------------------------------
       🧩 REGISTRATION LOGIC
       ---------------------------------------------------------- */
    public static void registerBlocks() {
        // 🟣 Create & register all Grand Shulker Box variants
        GRAND_SHULKER_BOX = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.PURPLE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_WHITE = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.WHITE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_ORANGE = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.ORANGE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_MAGENTA = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.MAGENTA,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_LIGHT_BLUE = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.LIGHT_BLUE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_YELLOW = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.YELLOW,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_LIME = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.LIME,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_PINK = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.PINK,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_GRAY = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.GRAY,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_LIGHT_GRAY = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.LIGHT_GRAY,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_CYAN = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.CYAN,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_PURPLE = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.PURPLE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_BLUE = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.BLUE,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_BROWN = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.BROWN,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_GREEN = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.GREEN,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_RED = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.RED,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );
        GRAND_SHULKER_BOX_BLACK = new GrandShulkerBoxBlock(
                net.minecraft.util.DyeColor.BLACK,
                FabricBlockSettings.copyOf(Blocks.SHULKER_BOX).nonOpaque()
        );

        // Register all blocks
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box"), GRAND_SHULKER_BOX);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_white"), GRAND_SHULKER_BOX_WHITE);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_orange"), GRAND_SHULKER_BOX_ORANGE);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_magenta"), GRAND_SHULKER_BOX_MAGENTA);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_light_blue"), GRAND_SHULKER_BOX_LIGHT_BLUE);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_yellow"), GRAND_SHULKER_BOX_YELLOW);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_lime"), GRAND_SHULKER_BOX_LIME);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_pink"), GRAND_SHULKER_BOX_PINK);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_gray"), GRAND_SHULKER_BOX_GRAY);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_light_gray"), GRAND_SHULKER_BOX_LIGHT_GRAY);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_cyan"), GRAND_SHULKER_BOX_CYAN);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_purple"), GRAND_SHULKER_BOX_PURPLE);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_blue"), GRAND_SHULKER_BOX_BLUE);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_brown"), GRAND_SHULKER_BOX_BROWN);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_green"), GRAND_SHULKER_BOX_GREEN);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_red"), GRAND_SHULKER_BOX_RED);
        Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_black"), GRAND_SHULKER_BOX_BLACK);

        // Register items (only for the main purple one)
        Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box"),
                new net.willowins.animewitchery.item.custom.GrandShulkerBoxItem(GRAND_SHULKER_BOX, new FabricItemSettings()));

        // Register block entity type with all variants
        GRAND_SHULKER_BOX_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(AnimeWitchery.MOD_ID, "grand_shulker_box_entity"),
                FabricBlockEntityTypeBuilder.create(
                        GrandShulkerBoxBlockEntity::new,
                        GRAND_SHULKER_BOX, GRAND_SHULKER_BOX_WHITE, GRAND_SHULKER_BOX_ORANGE, GRAND_SHULKER_BOX_MAGENTA,
                        GRAND_SHULKER_BOX_LIGHT_BLUE, GRAND_SHULKER_BOX_YELLOW, GRAND_SHULKER_BOX_LIME, GRAND_SHULKER_BOX_PINK,
                        GRAND_SHULKER_BOX_GRAY, GRAND_SHULKER_BOX_LIGHT_GRAY, GRAND_SHULKER_BOX_CYAN, GRAND_SHULKER_BOX_PURPLE,
                        GRAND_SHULKER_BOX_BLUE, GRAND_SHULKER_BOX_BROWN, GRAND_SHULKER_BOX_GREEN, GRAND_SHULKER_BOX_RED, GRAND_SHULKER_BOX_BLACK
                ).build()
        );

        AnimeWitchery.LOGGER.info("✅ Registered all Grand Shulker Box variants successfully.");
    }

    /* ----------------------------------------------------------
       ⚙️ HELPERS
       ---------------------------------------------------------- */
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

    public static void registerModBlocks() {
        AnimeWitchery.LOGGER.info("Registering ModBlocks for " + AnimeWitchery.MOD_ID);
        registerBlocks(); // Call the Grand Shulker Box registration
    }
}
