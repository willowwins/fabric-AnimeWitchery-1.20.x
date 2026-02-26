package net.willowins.animewitchery.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RevealedStatusEffect extends StatusEffect {
    public RevealedStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF0000); // Red color
    }

    // Simplest effect, just a marker.
}
