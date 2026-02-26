package net.willowins.animewitchery.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

public class ModEffects {
    public static final StatusEffect REVEALED = register("revealed", new RevealedStatusEffect());
    public static final StatusEffect BLEEDING = register("bleeding", new BleedingStatusEffect());

    private static StatusEffect register(String name, StatusEffect effect) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(AnimeWitchery.MOD_ID, name), effect);
    }

    public static void registerModEffects() {
        AnimeWitchery.LOGGER.info("Registering Mod Effects for " + AnimeWitchery.MOD_ID);
    }
}
