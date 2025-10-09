package net.willowins.animewitchery.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.item.ModArmorMaterials;

import java.util.Map;

/**
 * Grants full-set bonuses and effects depending on the armor material.
 * Automatically re-applies effects while worn, without stacking or duplication.
 */
public class ModArmorItem extends ArmorItem {
    private static final Multimap<ArmorMaterial, StatusEffectInstance> MATERIAL_TO_EFFECT_MAP =
            new ImmutableMultimap.Builder<ArmorMaterial, StatusEffectInstance>()
                    // Silver
                    .put(ModArmorMaterials.SILVER, new StatusEffectInstance(StatusEffects.SATURATION, 200, 0, false, false, false))
                    .put(ModArmorMaterials.SILVER, new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, false, false, false))
                    .put(ModArmorMaterials.SILVER, new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 255, false, false, false))

                    // Railgunner
                    .put(ModArmorMaterials.RAILGUNNER, new StatusEffectInstance(StatusEffects.SPEED, 200, 2, false, false, false))
                    .put(ModArmorMaterials.RAILGUNNER, new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 255, false, false, false))

                    // Obelisk
                    .put(ModArmorMaterials.OBELISK, new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, false, false, false))
                    .put(ModArmorMaterials.OBELISK, new StatusEffectInstance(StatusEffects.STRENGTH, 300, 1, false, false, false))
                    .put(ModArmorMaterials.OBELISK, new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 1, false, false, false))

                    // Resonant
                    .put(ModArmorMaterials.RESONANT, new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 0, false, false, false))
                    .put(ModArmorMaterials.RESONANT, new StatusEffectInstance(ModEffect.MANA_REGEN, 300, 2, false, false, false))
                    .build();

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    // === MAIN TICK ===
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        if (!hasFullSuitOfArmor(player)) {
            return;
        }

        ArmorMaterial material = getEquippedMaterial(player);
        if (material != null) {
            applyFullSetEffects(player, material);
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    // === EFFECT LOGIC ===
    private void applyFullSetEffects(PlayerEntity player, ArmorMaterial material) {
        for (Map.Entry<ArmorMaterial, StatusEffectInstance> entry : MATERIAL_TO_EFFECT_MAP.entries()) {
            if (entry.getKey() == material) {
                StatusEffectInstance effect = entry.getValue();
                StatusEffect type = effect.getEffectType();

                StatusEffectInstance current = player.getStatusEffect(type);
                // Refresh only if missing or nearly expired
                if (current == null || current.getDuration() <= 40) {
                    player.addStatusEffect(new StatusEffectInstance(
                            type,
                            effect.getDuration(),
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.shouldShowParticles(),
                            effect.shouldShowIcon()
                    ));
                }
            }
        }
    }

    private boolean hasFullSuitOfArmor(PlayerEntity player) {
        for (ItemStack armor : player.getInventory().armor) {
            if (armor.isEmpty() || !(armor.getItem() instanceof ArmorItem)) {
                return false;
            }
        }
        return true;
    }

    private ArmorMaterial getEquippedMaterial(PlayerEntity player) {
        ArmorItem first = (ArmorItem) player.getInventory().getArmorStack(0).getItem();
        ArmorMaterial type = first.getMaterial();

        for (ItemStack armor : player.getInventory().armor) {
            if (!(armor.getItem() instanceof ArmorItem armorItem) || armorItem.getMaterial() != type) {
                return null;
            }
        }
        return type;
    }
}
