package net.willowins.animewitchery.item.custom;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;

public class SoiledJacketItem extends ArmorItem {
    public SoiledJacketItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }
    // Logic for Luck vs Damage to be handled in ModArmorItem or Mixin
}
