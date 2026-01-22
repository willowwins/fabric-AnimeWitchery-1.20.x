package net.willowins.animewitchery.mixin.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.world.dimension.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class ParadiseLostSkyMixin {

    @Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
    private void normalizeSky(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        // Safe cast or check
        ClientWorld world = (ClientWorld) (Object) this;
        if (world.getRegistryKey().equals(ModDimensions.PARADISELOSTDIM_LEVEL_KEY)) {
            // Force Pure Black Sky
            cir.setReturnValue(new Vec3d(0.0, 0.0, 0.0));
        }
    }
}
