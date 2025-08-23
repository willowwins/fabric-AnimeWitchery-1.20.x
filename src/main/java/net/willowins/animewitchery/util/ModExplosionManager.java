// ModExplosionManager.java
package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ModExplosionManager {
    private static final java.util.ArrayList<TickableExplosion> ACTIVE = new java.util.ArrayList<>();

    public interface TickableExplosion {
        /** @return true when finished (remove from list). */
        boolean tickOnce(MinecraftServer server);
    }

    public static void add(TickableExplosion exp) {
        ACTIVE.add(exp);
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            java.util.ArrayList<TickableExplosion> done = new java.util.ArrayList<>();
            for (TickableExplosion e : ACTIVE) {
                if (e.tickOnce(server)) done.add(e);
            }
            if (!done.isEmpty()) ACTIVE.removeAll(done);
        });
    }
}
