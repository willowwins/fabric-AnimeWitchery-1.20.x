package net.willowins.animewitchery.world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.willowins.animewitchery.block.ModBlocks;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles deferred and safe obelisk registration once the world is running.
 */
public final class ObeliskWorldListener {

    private static final Queue<QueuedRegistration> PENDING = new ConcurrentLinkedQueue<>();
    private static boolean initialized = false;

    private record QueuedRegistration(ServerWorld world, BlockPos pos) {}

    public static void register() {

        // Safely process queue after server has started ticking
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!initialized && server.getTicks() > 20) {
                initialized = true;
                System.out.println("[AnimeWitchery] Obelisk listener initialized after startup.");
            }

            int processed = 0;
            final int MAX_PROCESSED_PER_TICK = 64; // Reduced from 128 to spread load better
            
            while (!PENDING.isEmpty() && processed < MAX_PROCESSED_PER_TICK) {
                QueuedRegistration entry = PENDING.poll();
                if (entry == null) break;

                ServerWorld world = entry.world();
                BlockPos pos = entry.pos();
                
                // Only register if still valid and chunk is actually loaded
                if (world != null && world.getChunkManager().isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)
                        && world.getBlockState(pos).isOf(ModBlocks.OBELISK)) {

                    ObeliskRegistry registry = ObeliskRegistry.get(world);
                    if (registry != null && !registry.contains(pos)) {
                        registry.register(pos);
                        // Debug print
                        // System.out.println("[AW] Registered obelisk at " + pos);
                    }
                }

                processed++;
            }
        });

        // Clean registry safely when world unloads
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            ObeliskRegistry registry = ObeliskRegistry.get(world);
            registry.getAll().removeIf(p -> !world.getBlockState(p).isOf(ModBlocks.OBELISK));
            System.out.println("[AW] Cleaned registry for " + world.getRegistryKey().getValue());
        });
    }

    /**
     * Public API: safely queue registration for a new obelisk.
     */
    public static void queueRegister(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) return;
        PENDING.add(new QueuedRegistration(world, pos.toImmutable()));
    }

    /**
     * Run a full cleanup on all worlds (e.g. on join/quit if desired).
     */
    public static void runCleanup(MinecraftServer server) {
        server.execute(() -> server.getWorlds().forEach(world -> {
            ObeliskRegistry registry = ObeliskRegistry.get(world);
            registry.getAll().removeIf(p -> !world.getBlockState(p).isOf(ModBlocks.OBELISK));
        }));
    }
}
