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
        public static final RegistryKey<DimensionType> PARADISELOSTDIM_DIM_TYPE = RegistryKey.of(
                        RegistryKeys.DIMENSION_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "paradiselostdim_type"));

        public static void bootstrapType(Registerable<DimensionType> context) {
                context.register(PARADISELOSTDIM_DIM_TYPE, new DimensionType(
                                OptionalLong.of(18000), // fixedTime (Midnight)
                                false, // hasSkylight (No sun - Cave Lighting - Pitch Black Ambient)
                                false, // hasCeiling
                                false, // ultraWarm
                                false, // natural (No day cycle logic)
                                1.0, // coordinateScale
                                true, // bedWorks
                                true, // respawnAnchorWorks
                                -64, // minY
                                640, // height
                                640, // logicalHeight
                                BlockTags.INFINIBURN_OVERWORLD, // infiniburn - allows nether portals
                                new Identifier(AnimeWitchery.MOD_ID, "paradiselost"), // effectsLocation (Custom Fog)
                                0.0f, // ambientLight (Set to 0 as requested)
                                new DimensionType.MonsterSettings(true, true, UniformIntProvider.create(0, 0), 0))); // piglinSafe
                                                                                                                      // =
                                                                                                                      // true
        }

        public static final RegistryKey<DimensionOptions> POCKET_DIM_KEY = RegistryKey.of(RegistryKeys.DIMENSION,
                        new Identifier(AnimeWitchery.MOD_ID, "pocket_dimension"));
        public static final RegistryKey<World> POCKET_LEVEL_KEY = RegistryKey.of(RegistryKeys.WORLD,
                        new Identifier(AnimeWitchery.MOD_ID, "pocket_dimension"));
        public static final RegistryKey<DimensionType> POCKET_DIM_TYPE = RegistryKey.of(
                        RegistryKeys.DIMENSION_TYPE,
                        new Identifier(AnimeWitchery.MOD_ID, "pocket_type"));

        public static void bootstrapPocketType(Registerable<DimensionType> context) {
                context.register(POCKET_DIM_TYPE, new DimensionType(
                                OptionalLong.of(18000), // has fixed time? Yes, Midnight.
                                true, // hasSkylight
                                false, // hasCeiling
                                false, // ultraWarm
                                false, // natural (False for fixed time)
                                1.0, // coordinateScale
                                true, // bedWorks (Allow sleeping/setting spawn)
                                true, // respawnAnchorWorks
                                0, // minY
                                256, // height
                                256, // logicalHeight
                                BlockTags.INFINIBURN_OVERWORLD,
                                DimensionTypes.OVERWORLD_ID, // effectsLocation
                                0.0f, // ambientLight
                                new DimensionType.MonsterSettings(false, false, UniformIntProvider.create(0, 0), 0))); // piglinSafe
        }
}