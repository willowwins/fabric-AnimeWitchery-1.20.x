package net.willowins.animewitchery.item.custom;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class CowboyHatItem extends ArmorItem {
    public CowboyHatItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    // Logic for trickshots/elemental to be handled in combat events/Revolver checks
}
