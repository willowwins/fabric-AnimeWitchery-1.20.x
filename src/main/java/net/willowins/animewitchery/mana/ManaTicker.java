package net.willowins.animewitchery.mana;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.willowins.animewitchery.AnimeWitchery;

public class ManaTicker {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ManaComponent mana = AnimeWitchery.AnimeWitcheryComponents.MANA.get(player);

                // Regenerate 1 mana per tick (20/sec).
                mana.regen(1);
            }
        });
    }
}

