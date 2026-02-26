package net.willowins.animewitchery.world.gen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.world.gen.feature.RosewillowMegaTreeFeature;
import net.willowins.animewitchery.world.gen.feature.FloatingIslandFeature;

public class ModFeatures {
    public static final Feature<DefaultFeatureConfig> ROSEWILLOW_MEGA_TREE = register("rosewillow_mega_tree",
            new RosewillowMegaTreeFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> FLOATING_ISLAND = register("floating_island",
            new FloatingIslandFeature(DefaultFeatureConfig.CODEC));

    private static <C extends net.minecraft.world.gen.feature.FeatureConfig, F extends Feature<C>> F register(
            String name, F feature) {
        return Registry.register(Registries.FEATURE, new Identifier(AnimeWitchery.MOD_ID, name), feature);
    }

    public static void registerModFeatures() {
        AnimeWitchery.LOGGER.info("Registering ModFeatures for " + AnimeWitchery.MOD_ID);
    }
}
