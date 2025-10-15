package net.willowins.animewitchery.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.effect.ModEffect;

public class ModPotions {
    
    // Mana Regen potions (3 levels)
    public static final Potion MANA_REGEN = registerPotion("mana_regen",
            new Potion(new StatusEffectInstance(ModEffect.MANA_REGEN, 900, 0))); // 45 seconds, level 1
    
    public static final Potion LONG_MANA_REGEN = registerPotion("long_mana_regen",
            new Potion(new StatusEffectInstance(ModEffect.MANA_REGEN, 1800, 0))); // 90 seconds, level 1
    
    public static final Potion STRONG_MANA_REGEN = registerPotion("strong_mana_regen",
            new Potion(new StatusEffectInstance(ModEffect.MANA_REGEN, 450, 1))); // 22.5 seconds, level 2

    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(Registries.POTION, new Identifier(AnimeWitchery.MOD_ID, name), potion);
    }

    public static void registerPotions() {
        AnimeWitchery.LOGGER.info("Registering Mod Potions for " + AnimeWitchery.MOD_ID);
    }
}

