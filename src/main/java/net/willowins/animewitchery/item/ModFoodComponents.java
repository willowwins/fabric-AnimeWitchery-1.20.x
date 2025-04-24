package net.willowins.animewitchery.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent LEMON = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(.25f)
            .build();

    public static final FoodComponent STRAWBERRY = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .build();

    public static final FoodComponent TART_CRUST = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .build();

    public static final FoodComponent UNBAKED_TART = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .build();

    public static final FoodComponent LEMON_TART = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE,1000,0),100f).build();

    public static final FoodComponent STRAWBERRY_TART = new FoodComponent.Builder().hunger(3).alwaysEdible().saturationModifier(1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,1000,0),100f).build();
    
}
