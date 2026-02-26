package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;

public class CrownItem extends ArmorItem {
    public CrownItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && slot == EquipmentSlot.HEAD.getEntitySlotId()) {
            // Logic for attracting mobs is better handled in the Mobs' AI goals,
            // checking if the player is wearing this item.
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
