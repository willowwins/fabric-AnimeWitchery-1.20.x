package net.willowins.animewitchery.mana;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.willowins.animewitchery.AnimeWitchery;

import java.util.List;

public class ManaTicker {
    private static int tickCounter = 0;
    private static final int REGEN_INTERVAL = 20; // Only regen every 20 ticks (1 second) instead of every tick
    
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            
            // Only process mana regeneration every 20 ticks to reduce overhead
            if (tickCounter % REGEN_INTERVAL == 0) {
                List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
                
                // Use enhanced for loop for better performance than iterator
                for (ServerPlayerEntity player : players) {
                    if (player != null && !player.isDisconnected()) {
                        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
                        if (mana != null) {
                            mana.regen(5);
                        }
                    }
                }
            }
        });
    }
}

