package net.willowins.animewitchery.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

public class ManaRegenEffect extends StatusEffect {
    public ManaRegenEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity player && !player.getWorld().isClient) {
            IManaComponent mana = ModComponents.PLAYER_MANA.get(player);

            // Base regen: 10 mana per tick
            // Multiplied exponentially: 10 * (4 ^ amplifier)
            int amount = (int) (10 * Math.pow(4, amplifier));
            mana.regen(amount);
        }
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Always true -> effect applied every tick
        return true;
    }
}
