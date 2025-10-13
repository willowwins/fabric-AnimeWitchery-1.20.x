package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * 🕰️ ServerScheduler — Lightweight tick-based scheduler for Fabric 1.20+
 *
 * Supports delayed and repeating tasks per server instance.
 * Integrates automatically with Fabric's tick and lifecycle events.
 *
 * Usage:
 *  - ServerScheduler.schedule(server, delayTicks, () -> { ... });
 *  - ServerScheduler.repeat(server, intervalTicks, () -> { ...; return true; });
 *    (return false to stop repeating)
 *
 * Automatically clears all tasks on world unload and server stop.
 */
public final class ServerScheduler {
    private static final Map<MinecraftServer, List<Task>> TASKS = new HashMap<>();
    private static boolean initialized = false;

    /** Represents a scheduled or repeating tick task */
    private static class Task {
        int ticksRemaining;
        final int interval;
        final Runnable single;
        final Supplier<Boolean> repeating;

        Task(int delay, Runnable single) {
            this.ticksRemaining = delay;
            this.interval = -1;
            this.single = single;
            this.repeating = null;
        }

        Task(int interval, Supplier<Boolean> repeating) {
            this.ticksRemaining = interval;
            this.interval = interval;
            this.single = null;
            this.repeating = repeating;
        }
    }

    // === Initialization Hook ===
    static {
        if (!initialized) {
            initialized = true;
            registerFabricHooks();
        }
    }

    /** Registers Fabric tick + cleanup listeners automatically. */
    private static void registerFabricHooks() {
        ServerTickEvents.END_WORLD_TICK.register(ServerScheduler::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerScheduler::onServerStopped);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> onServerStopped(server));
    }

    public static void tick(ServerWorld world) {
        MinecraftServer server = world.getServer();
        List<Task> tasks = TASKS.get(server);
        if (tasks == null || tasks.isEmpty()) return;

        // CopyOnWriteArrayList doesn't support iterator.remove(), so we collect tasks to remove
        List<Task> toRemove = new ArrayList<>();
        
        for (Task t : tasks) {
            if (--t.ticksRemaining <= 0) {
                try {
                    if (t.single != null) {
                        t.single.run();
                        toRemove.add(t);
                    } else if (t.repeating != null) {
                        boolean keep = t.repeating.get();
                        if (keep) {
                            t.ticksRemaining = t.interval;
                        } else {
                            toRemove.add(t);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[ServerScheduler] Task exception:");
                    e.printStackTrace();
                    toRemove.add(t);
                }
            }
        }
        
        // Remove completed tasks
        if (!toRemove.isEmpty()) {
            tasks.removeAll(toRemove);
        }
    }


    // === Public Task Methods ===

    /** Run once after delayTicks */
    public static void schedule(MinecraftServer server, int delayTicks, Runnable task) {
        TASKS.computeIfAbsent(server, s -> new CopyOnWriteArrayList<>()).add(new Task(delayTicks, task));
    }

    /** Repeat every intervalTicks until the supplier returns false */
    public static void repeat(MinecraftServer server, int intervalTicks, Supplier<Boolean> repeating) {
        TASKS.computeIfAbsent(server, s -> new CopyOnWriteArrayList<>()).add(new Task(intervalTicks, repeating));
    }

    /** Removes all tasks associated with this server */
    public static void clear(MinecraftServer server) {
        TASKS.remove(server);
    }

    /** Lifecycle cleanup */
    private static void onServerStopped(MinecraftServer server) {
        clear(server);
        System.out.println("[ServerScheduler] Cleared tasks for stopped server: " + server);
    }
}
