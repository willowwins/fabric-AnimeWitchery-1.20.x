package net.willowins.animewitchery.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.enchantments.custom.BootEnchantment;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
    Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap();
    Enchantment BOOT_ENCHANT = createEnchantment("boot_enchant", new BootEnchantment());

    static void init() {
        ENCHANTMENTS.keySet().forEach((enchantment) -> Registry.register(Registries.ENCHANTMENT, (Identifier)ENCHANTMENTS.get(enchantment), enchantment));
    }

    static <T extends Enchantment> T createEnchantment(String name, T enchantment) {
        ENCHANTMENTS.put(enchantment, new Identifier(AnimeWitchery.MOD_ID, name));
        return enchantment;
    }
}
