package net.willowins.animewitchery.mixin;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public class VillagerMobGriefingMixin {
    
    /**
     * Override mob griefing checks for villagers.
     * Allows villagers to pick up items and interact with blocks even when mobGriefing is false.
     */
    @Inject(method = "canGather", at = @At("HEAD"), cancellable = true)
    private void allowVillagerGathering(CallbackInfoReturnable<Boolean> cir) {
        // Always allow villagers to gather items, regardless of mobGriefing
        cir.setReturnValue(true);
    }
}

