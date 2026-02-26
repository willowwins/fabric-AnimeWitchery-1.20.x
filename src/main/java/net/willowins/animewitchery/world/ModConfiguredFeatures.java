package net.willowins.animewitchery.world;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.*;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;

import java.lang.module.Configuration;
import java.util.List;

public class ModConfiguredFeatures {

        public static final RegistryKey<ConfiguredFeature<?, ?>> SILVER_ORE_KEY = registerKey("silver_ore");
        public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_MOD_ORE_KEY = registerKey("nether_mod_ore");
        public static final RegistryKey<ConfiguredFeature<?, ?>> END_MOD_ORE_KEY = registerKey("end_mod_ore");
        public static final RegistryKey<ConfiguredFeature<?, ?>> ROSEWILLOW_KEY = registerKey("rosewillow_manual");
        public static final RegistryKey<ConfiguredFeature<?, ?>> ROSEWILLOW_MEGA_KEY = registerKey(
                        "rosewillow_mega_manual");

        public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
                RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
                RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
                RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
                RuleTest endReplaceables = new BlockMatchRuleTest(Blocks.END_STONE);

                List<OreFeatureConfig.Target> overworldSilverOres = List.of(
                                OreFeatureConfig.createTarget(stoneReplaceables,
                                                ModBlocks.SILVER_ORE.getDefaultState()),
                                OreFeatureConfig.createTarget(deepslateReplaceables,
                                                ModBlocks.DEEPSLATE_SILVER_ORE.getDefaultState()));

                List<OreFeatureConfig.Target> netherModOres = List
                                .of(OreFeatureConfig.createTarget(netherReplaceables,
                                                Blocks.BLUE_ICE.getDefaultState()));

                List<OreFeatureConfig.Target> endModOres = List.of(
                                OreFeatureConfig.createTarget(endReplaceables, Blocks.OBSIDIAN.getDefaultState()),
                                OreFeatureConfig.createTarget(endReplaceables,
                                                Blocks.CRYING_OBSIDIAN.getDefaultState()));

                register(context, SILVER_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldSilverOres, 10));
                register(context, NETHER_MOD_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherModOres, 10));
                register(context, END_MOD_ORE_KEY, Feature.ORE, new OreFeatureConfig(endModOres, 20));

                register(context, ROSEWILLOW_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                                net.minecraft.world.gen.stateprovider.BlockStateProvider.of(ModBlocks.ROSEWILLOW_LOG),
                                new net.minecraft.world.gen.trunk.StraightTrunkPlacer(5, 2, 0),
                                net.minecraft.world.gen.stateprovider.BlockStateProvider
                                                .of(ModBlocks.ROSEWILLOW_LEAVES),
                                new net.minecraft.world.gen.foliage.BlobFoliagePlacer(
                                                ConstantIntProvider.create(2),
                                                ConstantIntProvider.create(0), 3),
                                new TwoLayersFeatureSize(1, 0, 1))
                                .build());

                register(context, ROSEWILLOW_MEGA_KEY,
                                net.willowins.animewitchery.world.gen.ModFeatures.ROSEWILLOW_MEGA_TREE,
                                new DefaultFeatureConfig());
        }

        public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
                return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(AnimeWitchery.MOD_ID, name));
        }

        private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
                        Registerable<ConfiguredFeature<?, ?>> context,
                        RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
                context.register(key, new ConfiguredFeature<>(feature, configuration));
        }
}