package net.willowins.animewitchery.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.enchantments.custom.*;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
    Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap();
    Enchantment BOOT_ENCHANT = createEnchantment("boot_enchant", new BootEnchantment());
    Enchantment EXCAVATE_ENCHANT = createEnchantment("excavate_enchant", new ExcavationEnchantment());
    Enchantment BLAST_ENCHANT = createEnchantment("blast_enchant", new BlastEnchantment());
    Enchantment FLIGHT_ENCHANT = createEnchantment("flight_enchant", new FlightEnchantment());
    Enchantment SLIVER = createEnchantment("sliver_enchant", new NeedleThrowEnchantment());
    Enchantment LEAD_COATED_PAINT = createEnchantment("lead_coated_paint", new LeadCoatedEnchantment());

    static void init() {
        ENCHANTMENTS.keySet().forEach((enchantment) -> Registry.register(Registries.ENCHANTMENT, (Identifier)ENCHANTMENTS.get(enchantment), enchantment));
    }

    static <T extends Enchantment> T createEnchantment(String name, T enchantment) {
        ENCHANTMENTS.put(enchantment, new Identifier(AnimeWitchery.MOD_ID, name));
        return enchantment;
    }
}
