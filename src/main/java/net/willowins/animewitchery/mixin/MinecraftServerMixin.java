package net.willowins.animewitchery.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.willowins.animewitchery.util.MinecraftServerDuck;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerDuck {

        @Shadow
        @Final
        private Map<RegistryKey<World>, ServerWorld> worlds;

        @Shadow
        @Final
        protected LevelStorage.Session session;

        @Shadow
        @Final
        private Executor workerExecutor;

        @Shadow
        @Final
        protected SaveProperties saveProperties;

        @Shadow
        @Final
        private WorldGenerationProgressListener worldGenerationProgressListener;

        @Override
        public ServerWorld animewitchery$createPocketWorld(RegistryKey<World> worldKey, DimensionOptions options) {
                MinecraftServer server = (MinecraftServer) (Object) this;

                // Create UnmodifiableLevelProperties from the save properties
                net.minecraft.world.level.UnmodifiableLevelProperties worldProperties = new net.minecraft.world.level.UnmodifiableLevelProperties(
                                this.saveProperties,
                                this.saveProperties.getMainWorldProperties());

                // Create the new ServerWorld
                ServerWorld newWorld = new ServerWorld(
                                server,
                                this.workerExecutor,
                                this.session,
                                worldProperties,
                                worldKey,
                                options,
                                this.worldGenerationProgressListener,
                                false, // debugWorld
                                net.minecraft.world.biome.source.BiomeAccess
                                                .hashSeed(this.saveProperties.getGeneratorOptions().getSeed()),
                                ImmutableList.of(), // spawners
                                false, // shouldTickTime
                                null // RandomSequencesState
                );

                // Add to the server's world map
                this.worlds.put(worldKey, newWorld);

                // Fire world load event (Fabric API)
                net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.invoker().onWorldLoad(server,
                                newWorld);

                return newWorld;
        }
}
