package net.willowins.animewitchery.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class MarkedEffect extends StatusEffect {
    public MarkedEffect(StatusEffectCategory category, int color){super(category,color);}

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.getHealth()>1){
        entity.damage(entity.getDamageSources().magic(),1.0f);
        super.applyUpdateEffect(entity, amplifier);
    }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = 25 >> amplifier;
        if(i>0) {
            return duration % i == 0;
        }else {
        return true;
    }
}

}

