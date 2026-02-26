package net.willowins.animewitchery.enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class ChainsawEnchantment extends Enchantment {
    public ChainsawEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public boolean isAcceptableItem(net.minecraft.item.ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.item.AxeItem;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
