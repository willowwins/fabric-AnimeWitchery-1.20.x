// ModExplosionManager.java
package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ModExplosionManager {
    // Use ConcurrentLinkedQueue for better performance with concurrent access
    private static final ConcurrentLinkedQueue<TickableExplosion> ACTIVE = new ConcurrentLinkedQueue<>();

    public interface TickableExplosion {
        /** @return true when finished (remove from list). */
        boolean tickOnce(MinecraftServer server);
    }

    public static void add(TickableExplosion exp) {
        if (exp != null) {
            ACTIVE.add(exp);
        }
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ACTIVE.isEmpty()) return;
            
            // Use iterator for safe removal during iteration
            Iterator<TickableExplosion> iterator = ACTIVE.iterator();
            while (iterator.hasNext()) {
                TickableExplosion explosion = iterator.next();
                try {
                    if (explosion.tickOnce(server)) {
                        iterator.remove(); // Safe removal during iteration
                    }
                } catch (Exception e) {
                    System.err.println("[ModExplosionManager] Explosion tick exception:");
                    e.printStackTrace();
                    iterator.remove(); // Remove failed explosions
                }
            }
        });
    }
}
