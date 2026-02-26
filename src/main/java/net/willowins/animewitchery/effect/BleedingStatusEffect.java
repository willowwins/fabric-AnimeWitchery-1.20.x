package net.willowins.animewitchery.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingStatusEffect extends StatusEffect {
    public BleedingStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8B0000); // Dark Red
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 40 == 0; // Damage every 2 seconds
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), 1.0f + amplifier); // 1.0 damage + amp
    }
}
