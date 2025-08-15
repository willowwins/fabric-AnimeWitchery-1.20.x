package net.willowins.animewitchery.effect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class ModEffect {
    public static final StatusEffect BOUND = registerStatusEffect("bound",
            new BoundEffect(StatusEffectCategory.BENEFICIAL,0x000099).addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-5.0F, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final StatusEffect MARKED = registerStatusEffect("marked",
            new MarkedEffect(StatusEffectCategory.NEUTRAL,0x672c12));

    public static final StatusEffect VOID_BOUND = registerStatusEffect("void_bound",
            new VoidBoundEffect());

    public static StatusEffect registerStatusEffect(String name, StatusEffect statusEffect){
        return Registry.register(Registries.STATUS_EFFECT,new Identifier(AnimeWitchery.MOD_ID, name), statusEffect);
    }
    public static void registerEffects(){
        AnimeWitchery.LOGGER.info("Registering Mod Effects For " + AnimeWitchery.MOD_ID);
    }
}
