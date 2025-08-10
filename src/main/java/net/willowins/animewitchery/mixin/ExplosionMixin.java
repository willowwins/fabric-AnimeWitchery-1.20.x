package net.willowins.animewitchery.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.willowins.animewitchery.events.BarrierExplosionHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow @Final private World world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private float power;

    // Inject at the start of collectBlocksAndDamageEntities to cancel if barrier absorbs
    @Inject(method = "collectBlocksAndDamageEntities", at = @At("HEAD"), cancellable = true)
    private void animewitchery$absorbByBarrier(CallbackInfo ci) {
        if (this.world instanceof ServerWorld serverWorld) {
            Vec3d center = new Vec3d(this.x, this.y, this.z);
            if (BarrierExplosionHandler.tryAbsorb(serverWorld, center, this.power)) {
                ci.cancel();
            }
        }
    }
}


