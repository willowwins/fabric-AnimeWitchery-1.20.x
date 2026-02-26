package net.willowins.animewitchery.util;

public class ButcherContext {
    public static final ThreadLocal<Boolean> IS_PASSIVE_LOOTING = ThreadLocal.withInitial(() -> false);
}
