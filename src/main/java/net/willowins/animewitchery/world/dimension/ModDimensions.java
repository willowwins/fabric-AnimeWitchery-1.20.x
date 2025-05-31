package net.willowins.animewitchery.world.dimension;

import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.willowins.animewitchery.AnimeWitchery;

import java.util.OptionalLong;

public class ModDimensions {
    public static final RegistryKey<DimensionOptions> PARADISELOSTDIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
            new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim"));
    public static final RegistryKey<World> PARADISELOSTDIM_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
            new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim"));
    public static final RegistryKey<DimensionType> PARADISELOSTDIM_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim_type"));

    public static void bootstrapType(Registerable<DimensionType> context) {
        context.register(PARADISELOSTDIM_DIM_TYPE, new DimensionType(
                OptionalLong.of(18000), // fixedTime
                true, // hasSkylight
                false, // hasCeiling
                false, // ultraWarm
                true, // natural
                1.0, // coordinateScale
                true, // bedWorks
                false, // respawnAnchorWorks
                -64, // minY
                256, // height
                256, // logicalHeight
                BlockTags.WITHER_IMMUNE, // immune to wither
                DimensionTypes.OVERWORLD_ID, // effectsLocation
                0.5f, // ambientLight
                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0)));
    }
}