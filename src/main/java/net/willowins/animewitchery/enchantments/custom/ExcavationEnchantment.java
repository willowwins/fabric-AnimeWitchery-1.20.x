package net.willowins.animewitchery.enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ExcavationEnchantment extends Enchantment {

    public ExcavationEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }


    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        // Accept enchanted books and all digger tools (pickaxes, shovels, axes, hoes)
        return stack.isOf(Items.ENCHANTED_BOOK) || super.isAcceptableItem(stack);
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
