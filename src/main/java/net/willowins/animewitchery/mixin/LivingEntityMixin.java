package net.willowins.animewitchery.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public class LivingEntityMixin {
    // No tick injection needed anymore — keybinding will handle this.
}
