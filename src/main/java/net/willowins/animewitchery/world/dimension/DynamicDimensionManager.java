package net.willowins.animewitchery.world.dimension;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.willowins.animewitchery.AnimeWitchery;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.util.math.random.RandomSeed;
import com.google.common.collect.ImmutableList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import net.willowins.animewitchery.util.MinecraftServerDuck;

public class DynamicDimensionManager {

    public static ServerWorld getOrCreatePocketDimension(MinecraftServer server, UUID pocketUuid) {
        Identifier dimId = new Identifier(AnimeWitchery.MOD_ID, "pocket_" + pocketUuid.toString());
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, dimId);

        ServerWorld existingWorld = server.getWorld(worldKey);
        if (existingWorld != null) {
            return existingWorld;
        }

        // Create new dimension
        return createPocketDimension(server, worldKey);
    }

    private static ServerWorld createPocketDimension(MinecraftServer server, RegistryKey<World> worldKey) {
        // Retrieve the Pocket Dimension Type
        RegistryKey<DimensionType> dimTypeKey = ModDimensions.POCKET_DIM_TYPE;

        // We need DimensionOptions.
        // We can try to copy from the existing level registry or create new.
        // This requires access to the Registry<DimensionOptions>.

        // Simpler approach: Create options manually if possible, or fetch from
        // registry.
        // Let's try to get the DimensionOptions from the registry.
        // But since we are creating a new DYNAMIC dimension, we need to construct a new
        // DimensionOptions
        // that points to our DimensionType and uses a ChunkGenerator.

        // Let's use the Overworld's ChunkGenerator but with our DimensionType?
        // Or better: The Pocket Dimension likely has a specific ChunkGenerator
        // (Void/Flat).

        // Fetch the registry access
        DynamicRegistryManager registryManager = server.getRegistryManager();

        // Logic to create ChunkGenerator (e.g., FlatChunkGenerator or a custom one)
        // For Pocket Dim, we want a Void world.
        // Let's assume we can get the ChunkGenerator from the existing
        // "pocket_dimension" if it exists in the registry options,
        // OR we create a new void generator.

        // FALLBACK: Use the Overworld generator for now if we can't easily construct a
        // Void one here without more context.
        // BUT the user wants a VOID world for pockets.
        // Let's try to look up "animewitchery:pocket_dimension" options and copy its
        // generator.

        DimensionOptions protoOptions = server.getCombinedDynamicRegistries().getCombinedRegistryManager()
                .get(RegistryKeys.DIMENSION).get(ModDimensions.POCKET_DIM_KEY);

        if (protoOptions == null) {
            AnimeWitchery.LOGGER.error("Could not find prototype Pocket Dimension Options!");
            return null;
        }

        // Create new options with the same generator and type
        DimensionOptions newOptions = new DimensionOptions(
                protoOptions.dimensionTypeEntry(),
                protoOptions.chunkGenerator());

        // Use the Duck to create the world
        return ((MinecraftServerDuck) server).animewitchery$createPocketWorld(worldKey, newOptions);
    }
}
