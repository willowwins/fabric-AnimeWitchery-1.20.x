package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;

public class BarrierBlockProtectionHandler {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            // Only handle server-side and non-creative players
            if (!(world instanceof ServerWorld serverWorld)) return true;
            if (player.isCreative()) return true; // Creative mode bypasses protection
            
            // Check if this block is inside any active barrier
            BarrierCircleBlockEntity barrier = findActiveBarrierContaining(serverWorld, pos);

            if (barrier != null) {
                // Check if player is authorized
                if (!barrier.isPlayerAllowedByUuid(player.getUuid())) {
                    // Player is not authorized - prevent breaking
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.sendMessage(Text.literal("§c This area is protected by a barrier!"), true);
                    }
                    return false;
                }
            }
            
            // No barrier or player is authorized - allow breaking
            return true;
        });
    }
    
    /**
     * Find an active barrier circle that contains the given position
     */
    private static BarrierCircleBlockEntity findActiveBarrierContaining(ServerWorld world, BlockPos blockPos) {
        Vec3d position = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
        
        // Search nearby chunks for barrier circles
        // We need to search a reasonable radius - let's use 128 blocks
        int searchRadius = 256;
        int centerX = blockPos.getX();
        int centerZ = blockPos.getZ();
        
        for (int x = centerX - searchRadius; x <= centerX + searchRadius; x += 16) {
            for (int z = centerZ - searchRadius; z <= centerZ + searchRadius; z += 16) {
                // Search all block entities in this chunk column
                for (BlockEntity be : world.getChunk(x >> 4, z >> 4).getBlockEntities().values()) {
                    if (be instanceof BarrierCircleBlockEntity barrier) {
                        // Check if this barrier is active and contains the position
                        if (barrier.isBarrierFunctional() && barrier.containsPosition(position)) {
                            return barrier;
                        }
                    }
                }
            }
        }
        
        return null;
    }
}




