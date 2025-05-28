package net.willowins.animewitchery.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BoundEffect extends StatusEffect {
   public BoundEffect(StatusEffectCategory category, int color){super(category,color);}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setMovementSpeed(0f);
       entity.setGlowing(false);
       super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
