package net.willowins.animewitchery.enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FlightEnchantment extends Enchantment {

    public FlightEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }


    @Override
    public boolean isCursed() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false; // Disabled - only obtainable through alchemy table
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false; // Disabled - only obtainable through alchemy table
    }
    
    @Override
    public boolean isTreasure() {
        return true; // Mark as treasure to prevent enchanting table generation
    }
}
