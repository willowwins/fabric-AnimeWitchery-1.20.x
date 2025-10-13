package net.willowins.animewitchery.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import net.willowins.animewitchery.world.ModPlacedFeatures;

public class ModOreGeneration {
    public static void generateOres(){
        try {
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(),
                    GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.SILVER_ORE_PLACED_KEY);

            BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(),
                    GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.NETHER_MOD_ORE_PLACED_KEY);

            BiomeModifications.addFeature(BiomeSelectors.foundInTheEnd(),
                    GenerationStep.Feature.UNDERGROUND_ORES, ModPlacedFeatures.END_MOD_ORE_PLACED_KEY);
        } catch (Exception e) {
            net.willowins.animewitchery.AnimeWitchery.LOGGER.error("Failed to register ore generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
