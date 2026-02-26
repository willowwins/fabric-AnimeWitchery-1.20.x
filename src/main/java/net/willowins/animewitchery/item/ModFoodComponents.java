package net.willowins.animewitchery.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.willowins.animewitchery.effect.ModEffect;

public class ModFoodComponents {
        public static final FoodComponent LEMON = new FoodComponent.Builder().hunger(3).saturationModifier(1f)
                        .build();

        public static final FoodComponent STRAWBERRY = new FoodComponent.Builder().hunger(3).saturationModifier(1f)
                        .build();

        public static final FoodComponent TART_CRUST = new FoodComponent.Builder().hunger(3).saturationModifier(1f)
                        .build();

        public static final FoodComponent UNBAKED_TART = new FoodComponent.Builder().hunger(3).saturationModifier(1f)
                        .build();

        public static final FoodComponent LEMON_TART = new FoodComponent.Builder().hunger(8).alwaysEdible()
                        .saturationModifier(1.5f)
                        .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1000, 0), 100f).build();

        public static final FoodComponent STRAWBERRY_TART = new FoodComponent.Builder().hunger(8).alwaysEdible()
                        .saturationModifier(1.5f)
                        .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1000, 0), 100f).build();

        public static final FoodComponent ROSEWILLOW_BLOSSOM = new FoodComponent.Builder().hunger(2)
                        .saturationModifier(0.2f)
                        .statusEffect(new StatusEffectInstance(ModEffect.MANA_REGEN, 600, 0), 100f).build(); // 30
                                                                                                             // seconds
                                                                                                             // (600
                                                                                                             // ticks)

}
