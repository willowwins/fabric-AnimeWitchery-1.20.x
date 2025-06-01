package net.willowins.animewitchery.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class FakeServerPlayerInteractionManager extends ServerPlayerInteractionManager {
    public FakeServerPlayerInteractionManager(ServerPlayerEntity player) {
        super(player);
    }

    // Expose a public method to set game mode
    public void setGameModePublic(GameMode mode) {
        super.setGameMode(mode, mode);
    }
}




