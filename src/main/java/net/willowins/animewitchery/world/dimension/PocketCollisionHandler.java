package net.willowins.animewitchery.world.dimension;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class PocketCollisionHandler {
    private static final double BORDER_LIMIT = 499.0; // 500 - 1 block buffer

    public static void tick(ServerWorld world) {
        if (!world.getRegistryKey().equals(ModDimensions.POCKET_LEVEL_KEY)) {
            return;
        }

        for (ServerPlayerEntity player : world.getPlayers()) {
            double x = player.getX();
            double z = player.getZ();

            // Calculate Center of current grid cell (Centered Logic)
            // Cell Width = 1000.
            // Center is multiple of 1000.

            double cellX = Math.floor((x + 500) / 1000.0);
            double cellZ = Math.floor((z + 500) / 1000.0);

            double centerX = cellX * 1000.0;
            double centerZ = cellZ * 1000.0;

            boolean changed = false;
            double newX = x;
            double newZ = z;
            double pushX = 0;
            double pushZ = 0;

            // Check X bounds
            if (x < centerX - BORDER_LIMIT) {
                newX = centerX - BORDER_LIMIT + 0.1;
                pushX = 0.5;
                changed = true;
            } else if (x > centerX + BORDER_LIMIT) {
                newX = centerX + BORDER_LIMIT - 0.1;
                pushX = -0.5;
                changed = true;
            }

            // Check Z bounds
            if (z < centerZ - BORDER_LIMIT) {
                newZ = centerZ - BORDER_LIMIT + 0.1;
                pushZ = 0.5;
                changed = true;
            } else if (z > centerZ + BORDER_LIMIT) {
                newZ = centerZ + BORDER_LIMIT - 0.1;
                pushZ = -0.5;
                changed = true;
            }

            if (changed) {
                player.teleport(newX, player.getY(), newZ);
                player.setVelocity(pushX, player.getVelocity().y, pushZ);
                player.velocityModified = true;
            }
        }
    }
}
