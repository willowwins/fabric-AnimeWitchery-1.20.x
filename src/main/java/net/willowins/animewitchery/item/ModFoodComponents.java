package net.willowins.animewitchery.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent LEMON = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(.25f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE,2000,2),100f).build();

    public static final FoodComponent STRAWBERRY = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,1000,2),100f).build();
    
}
