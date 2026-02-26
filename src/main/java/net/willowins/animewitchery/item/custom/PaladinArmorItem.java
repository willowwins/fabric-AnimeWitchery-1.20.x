package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.entity.EquipmentSlot;

import java.util.List;

public class PaladinArmorItem extends ArmorItem {
    public PaladinArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && slot == EquipmentSlot.CHEST.getEntitySlotId()) {
            if (isFullSet(player)) {
                // Healing Aura
                if (player.age % 100 == 0) { // Every 5 seconds
                    List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(PlayerEntity.class,
                            player.getBoundingBox().expand(10), p -> p != player);
                    for (PlayerEntity p : nearbyPlayers) {
                        p.addStatusEffect(
                                new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0, false, false, true));
                    }
                    // Heal self too? Paladin usually selfless, but sure.
                    player.addStatusEffect(
                            new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0, false, false, true));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private boolean isFullSet(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModItems.PALADIN_HELMET &&
                player.getEquippedStack(EquipmentSlot.CHEST).getItem() == ModItems.PALADIN_CHESTPLATE &&
                player.getEquippedStack(EquipmentSlot.LEGS).getItem() == ModItems.PALADIN_LEGGINGS &&
                player.getEquippedStack(EquipmentSlot.FEET).getItem() == ModItems.PALADIN_BOOTS;
    }
}
