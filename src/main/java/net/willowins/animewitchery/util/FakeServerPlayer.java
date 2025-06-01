package net.willowins.animewitchery.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;

import java.lang.reflect.Field;

public class FakeServerPlayer extends ServerPlayerEntity {

    public FakeServerPlayer(MinecraftServer server, ServerWorld world, GameProfile profile) {
        super(server, world, profile);

        try {
            Field interactionManagerField = ServerPlayerEntity.class.getDeclaredField("interactionManager");
            interactionManagerField.setAccessible(true);

            FakeServerPlayerInteractionManager customManager = new FakeServerPlayerInteractionManager(this);

            customManager.setGameModePublic(GameMode.CREATIVE);

            interactionManagerField.set(this, customManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set interactionManager for FakeServerPlayer", e);
        }
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public boolean canModifyBlocks() {
        return true;
    }
}
