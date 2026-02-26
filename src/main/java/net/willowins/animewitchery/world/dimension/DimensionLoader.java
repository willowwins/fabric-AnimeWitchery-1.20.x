package net.willowins.animewitchery.world.dimension;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class DimensionLoader {
    public static void register() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            // Only run on Overworld load (once per server start)
            if (world.getRegistryKey() == World.OVERWORLD) {
                PocketManager manager = PocketManager.getServerState(world);
                for (java.util.UUID uuid : manager.getPocketUuids()) {
                    DynamicDimensionManager.getOrCreatePocketDimension(server, uuid);
                }
            }
        });
    }
}
