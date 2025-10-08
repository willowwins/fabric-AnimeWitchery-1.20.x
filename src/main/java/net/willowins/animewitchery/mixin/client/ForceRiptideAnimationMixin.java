package net.willowins.animewitchery.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class ForceRiptideAnimationMixin {
    @Inject(method = "handleStatus", at = @At("HEAD"))
    private void animewitchery$forceRiptideSpin(byte status, CallbackInfo ci) {
        if (status == 54) {
            Entity self = (Entity)(Object)this;
            ClientPlayerEntity local = MinecraftClient.getInstance().player;

            if (local != null && self.getId() == local.getId()) {
                ((LivingEntityRiptideAccessor)self).setRiptideTicks(20); // ✅ safe access
                ((LivingEntityRiptideAccessor)self).invokeSetLivingFlag(4, true); // mark as riptiding
                System.out.println("[AnimeWitchery] Forced riptide spin on client");
            }
        }
    }
}
