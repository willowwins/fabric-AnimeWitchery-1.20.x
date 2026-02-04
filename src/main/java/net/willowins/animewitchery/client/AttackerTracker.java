package net.willowins.animewitchery.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttackerTracker {
    private static final Map<UUID, Long> attackers = new HashMap<>();
    private static final long DURATION = 200; // 10 seconds

    public static void mark(UUID uuid, long time) {
        attackers.put(uuid, time + DURATION);
    }

    public static boolean isMarked(UUID uuid, long time) {
        return attackers.containsKey(uuid) && attackers.get(uuid) > time;
    }

    public static void cleanup(long time) {
        attackers.entrySet().removeIf(entry -> entry.getValue() <= time);
    }
}
