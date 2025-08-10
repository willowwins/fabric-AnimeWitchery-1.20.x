package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;

/**
 * Simple explosion absorber: scans worlds each tick for pending explosions via a lightweight hook.
 * Since Fabric ExplosionEvents may vary by version, we implement a conservative radius check by
 * intercepting on server tick by inspecting the world's exploding list is not available here.
 *
 * Instead, we provide a static API to be called from mixins or explicit explosion creations.
 */
public final class BarrierExplosionHandler implements ServerTickEvents.EndWorldTick {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(new BarrierExplosionHandler());
    }

    private BarrierExplosionHandler() {}

    @Override
    public void onEndTick(ServerWorld world) {
        // No-op. Placeholder for future advanced interception if needed.
    }

    /**
     * Call this before spawning an explosion or from a mixin on Explosion.collectBlocksAndDamageEntities.
     * Returns true if the explosion should be cancelled (fully absorbed by a barrier).
     */
    public static boolean tryAbsorb(ServerWorld world, Vec3d pos, float power) {
        BarrierCircleBlockEntity barrier = BarrierCircleBlockEntity.findBarrierAt(world, pos);
        if (barrier != null) {
            barrier.absorbExplosion(pos, power);
            // Optional: play a shield hit effect
            world.playSound(null, barrier.getPos(), net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_FALL, net.minecraft.sound.SoundCategory.BLOCKS, 0.6f, 0.8f);
            return true; // cancel explosion
        }
        return false;
    }
}


