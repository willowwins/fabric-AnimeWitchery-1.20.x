package net.willowins.animewitchery.world.gen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.world.gen.foliage.RosewillowFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<RosewillowFoliagePlacer> ROSEWILLOW_FOLIAGE_PLACER = register(
            "rosewillow_foliage_placer", RosewillowFoliagePlacer.CODEC);

    private static <P extends net.minecraft.world.gen.foliage.FoliagePlacer> FoliagePlacerType<P> register(String name,
            com.mojang.serialization.Codec<P> codec) {
        return Registry.register(Registries.FOLIAGE_PLACER_TYPE,
                new net.minecraft.util.Identifier(AnimeWitchery.MOD_ID, name), new FoliagePlacerType<>(codec));
    }

    public static void registerModFoliagePlacerTypes() {
        AnimeWitchery.LOGGER.info("Registering Mod Foliage Placer Types for " + AnimeWitchery.MOD_ID);
    }
}
