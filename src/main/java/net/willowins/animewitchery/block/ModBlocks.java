package net.willowins.animewitchery.block;

import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.custom.*;
import net.willowins.animewitchery.block.custom.ItemActionBlock;
import net.willowins.animewitchery.block.custom.ObeliskBlock;
import net.willowins.animewitchery.block.custom.ActiveObeliskBlock;
import net.willowins.animewitchery.block.custom.BossObeliskBlock;

import java.util.Objects;

public class ModBlocks {

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

    public static final Block SILVER_BLOCK =registerBlock("silver_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block PARTICLE_BLOCK =registerBlock("particle_block",
            new ParticleBeamBlock());
    public static final Block PARTICLE_SINK_BLOCK =registerBlock("particle_sink_block",
            new ParticleSinkBlock());

    public static final Block EFFIGY_FOUNTAIN =registerBlock("effigy_fountain",
            new EffigyFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

    public static final Block DECORATIVE_FOUNTAIN =registerBlock("decorative_fountain",
            new DecorativeFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

public static final Block BINDING_SPELL =registerBlock("binding_spell",
            new BindingSpellBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

public static final Block ALCHEMY_TABLE =registerBlock("alchemy_table",
            new AlchemyTableBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

    public static final Block ACTIVE_EFFIGY_FOUNTAIN =registerBlock("active_effigy_fountain",
            new ActiveEffigyFountainBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

    public static final Block ACTIVE_BINDING_SPELL =registerBlock("active_binding_spell",
            new ActiveBindingSpellBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

    public static final Block OBELISK =registerBlock("obelisk",
            new ObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

    public static final Block ACTIVE_OBELISK =registerBlock("active_obelisk",
            new ActiveObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

    public static final Block BOSS_OBELISK = registerBlock("boss_obelisk",
            new BossObeliskBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque().hardness(-1f)));

    public static final Block BARRIER_CIRCLE =registerBlock("barrier_circle",
            new BarrierCircleBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

    public static final Block BARRIER_DISTANCE_GLYPH = registerBlock("barrier_distance_glyph",
            new BarrierDistanceGlyphBlock(FabricBlockSettings.copyOf(Blocks.STONE).nonOpaque()));

    public static final Block SOUND_BLOCK =registerBlock("sound_block",
            new SoundBlock(FabricBlockSettings.copyOf(Blocks.JUKEBOX)));

    public static final Block SOUND_BLOCK2 =registerBlock("sound_block2",
            new SoundBlock2(FabricBlockSettings.copyOf(Blocks.JUKEBOX)));

    public static final Block SILVER_STAIRS =registerBlock("silver_stairs",
            new StairsBlock(ModBlocks.SILVER_BLOCK.getDefaultState(),FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block SILVER_SLAB =registerBlock("silver_slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block SILVER_BUTTON =registerBlock("silver_button",
            new ButtonBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), BlockSetType.IRON,30,true));
    public static final Block SILVER_PRESSURE_PLATE =registerBlock("silver_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK),BlockSetType.IRON));

    public static final Block SILVER_FENCE =registerBlock("silver_fence",
            new FenceBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block SILVER_FENCE_GATE =registerBlock("silver_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK), WoodType.DARK_OAK));
    public static final Block SILVER_WALL =registerBlock("silver_wall",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block SILVER_DOOR =registerBlock("silver_door",
            new DoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_DOOR),BlockSetType.IRON));
    public static final Block SILVER_TRAPDOOR =registerBlock("silver_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.IRON_TRAPDOOR),BlockSetType.IRON));

    public static final Block SILVER_ORE = registerBlock("silver_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.STONE).strength(1.5f), UniformIntProvider.create(5,7)));

    public static final Block CHARCOAL_BLOCK = registerBlock("charcoal_block",
            new Block(FabricBlockSettings.copyOf(Blocks.COAL_BLOCK)));

    public static final Block DEEPSLATE_SILVER_ORE = registerBlock("deepslate_silver_ore",
            new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.DEEPSLATE), UniformIntProvider.create(7,10)));

    public static final Block STRAWBERRY_CROP = Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "strawberry_crop"),
            new StrawberryCropBlock(FabricBlockSettings.copyOf(Blocks.WHEAT)));

    public static final Block LEMON_CROP = Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, "lemon_crop"),
            new LemonCropBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_SAPLING)));

    public static final Block FLOAT_BLOCK = registerBlock("float_block",
            new FloatBlock(FabricBlockSettings.copyOf(Blocks.BARRIER)));


    private static Block registerBlock(String name, Block block){
        if (!Objects.equals(name, "float_block")) {
            registerBlockItem(name, block);
        }
        return Registry.register(Registries.BLOCK, new Identifier(AnimeWitchery.MOD_ID, name), block);
    }

    private static Item registerBlockItem (String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(AnimeWitchery.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));

    }

    public static void registerModBlocks(){
        AnimeWitchery.LOGGER.info("Registering ModBlocks for " + AnimeWitchery.MOD_ID);
    }
}
