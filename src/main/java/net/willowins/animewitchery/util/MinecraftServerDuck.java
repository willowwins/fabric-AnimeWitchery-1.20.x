package net.willowins.animewitchery.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public interface MinecraftServerDuck {
    ServerWorld animewitchery$createPocketWorld(RegistryKey<World> worldKey, DimensionOptions options);
}
