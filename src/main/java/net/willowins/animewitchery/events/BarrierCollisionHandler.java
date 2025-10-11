package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;

/**
 * Handles collision with barrier walls to prevent unauthorized entry
 */
public class BarrierCollisionHandler implements ServerTickEvents.EndWorldTick {
    
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(new BarrierCollisionHandler());
    }
    
    private BarrierCollisionHandler() {}
    
    @Override
    public void onEndTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            checkBarrierCollision(player);
        }
    }
    
    private void checkBarrierCollision(ServerPlayerEntity player) {
        Vec3d playerPos = player.getPos();
        Vec3d playerVelocity = player.getVelocity();
        
        // Find if player is near a barrier (checks horizontal position only)
        BarrierCircleBlockEntity barrier = BarrierCircleBlockEntity.findBarrierAt(player.getWorld(), playerPos);
        
        if (barrier != null && !barrier.isPlayerAllowedByUuid(player.getUuid())) {
            // Barrier walls extend from bedrock to build height (y=-64 to y=320)
            // Check if player is trying to enter from outside at ANY height
            Vec3d nextPos = playerPos.add(playerVelocity);
            boolean isInsideNow = barrier.containsPosition(playerPos);
            boolean willBeInside = barrier.containsPosition(nextPos);
            
            // If player is outside trying to get in, block them (at any Y level)
            if (!isInsideNow && willBeInside) {
                // Cancel horizontal velocity toward barrier
                player.setVelocity(0, player.getVelocity().y, 0);
                
                // Push player back outward from barrier center
                double[] extents = barrier.computeExtents();
                Vec3d pushDirection = getPushDirectionFromBarrier(playerPos, extents, barrier);
                player.setVelocity(player.getVelocity().add(pushDirection.multiply(0.2)));
            }
        }
    }
    
    private Vec3d getPushDirectionFromBarrier(Vec3d pos, double[] extents, BarrierCircleBlockEntity barrier) {
        // Simple outward push from center
        double centerX = (extents[0] + extents[1]) / 2.0;
        double centerZ = (extents[2] + extents[3]) / 2.0;
        
        double dx = pos.x - centerX;
        double dz = pos.z - centerZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        if (dist > 0) {
            return new Vec3d(-dx / dist, 0, -dz / dist);
        }
        
        return Vec3d.ZERO;
    }
}

