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
    private static final Multimap<ArmorMaterial, StatusEffectInstance> MATERIAL_TO_EFFECT_MAP = new ImmutableMultimap.Builder<ArmorMaterial, StatusEffectInstance>()
            // Silver
            .put(ModArmorMaterials.SILVER,
                    new StatusEffectInstance(StatusEffects.SATURATION, 200, 0, false, false, false))
            .put(ModArmorMaterials.SILVER, new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, false, false, false))
            .put(ModArmorMaterials.SILVER,
                    new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 255, false, false, false))

            // Railgunner
            .put(ModArmorMaterials.RAILGUNNER,
                    new StatusEffectInstance(StatusEffects.SPEED, 200, 2, false, false, false))
            .put(ModArmorMaterials.RAILGUNNER,
                    new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 255, false, false, false))

            // Obelisk
            .put(ModArmorMaterials.OBELISK,
                    new StatusEffectInstance(StatusEffects.NIGHT_VISION, 300, 0, false, false, false))
            .put(ModArmorMaterials.OBELISK,
                    new StatusEffectInstance(StatusEffects.STRENGTH, 300, 1, false, false, false))
            .put(ModArmorMaterials.OBELISK,
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 1, false, false, false))

            // Resonant
            .put(ModArmorMaterials.RESONANT,
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 300, 0, false, false, false))
            .put(ModArmorMaterials.RESONANT,
                    new StatusEffectInstance(ModEffect.MANA_REGEN, 300, 2, false, false, false))

            // Haloic
            .put(ModArmorMaterials.HALOIC,
                    new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1, false, false, false))
            .put(ModArmorMaterials.HALOIC,
                    new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0, false, false, false))
            .build();

    public ModArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    // === MAIN TICK ===
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        // Client-side visual effects (Aura)
        if (world.isClient()) {
            if (hasFullSuitOfArmor(player) && getEquippedMaterial(player) == ModArmorMaterials.HALOIC) {
                spawnHaloicAura(world, player);
            }
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }

        // Server-side status effects
        if (!hasFullSuitOfArmor(player)) {
            return;
        }

        ArmorMaterial material = getEquippedMaterial(player);
        if (material != null) {
            applyFullSetEffects(player, material);
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    // === VISUALS ===
    private void spawnHaloicAura(World world, PlayerEntity player) {
        // Only spawn particles occasionally to avoid clutter, or every tick for intense
        // effect
        // "DBZ style" implies intensity.

        double x = player.getX() + (world.random.nextDouble() - 0.5) * 1.5;
        double y = player.getY() + world.random.nextDouble() * 2.0;
        double z = player.getZ() + (world.random.nextDouble() - 0.5) * 1.5;

        // Rising motion
        team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
                .create(team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry.WISP_PARTICLE)
                .setScaleData(
                        team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.3f, 0f).build())
                .setTransparencyData(
                        team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.6f, 0f).build())
                .setColorData(team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
                        .create(new java.awt.Color(255, 215, 0), new java.awt.Color(255, 69, 0)) // Gold to Red-Orange
                        .setCoefficient(1.0f).setEasing(team.lodestar.lodestone.systems.easing.Easing.EXPO_OUT).build())
                .setLifetime(25)
                .addMotion(0, 0.1 + world.random.nextDouble() * 0.1, 0)
                .enableNoClip()
                .spawn(world, x, y, z);

        // Occasional "Spark" or "Lightning" effect
        if (world.random.nextInt(20) == 0) {
            team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
                    .create(team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry.SPARKLE_PARTICLE)
                    .setScaleData(
                            team.lodestar.lodestone.systems.particle.data.GenericParticleData.create(0.5f, 0f).build())
                    .setColorData(team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
                            .create(new java.awt.Color(255, 255, 200), new java.awt.Color(255, 215, 0)).build())
                    .setLifetime(15)
                    .spawn(world, player.getX() + (world.random.nextDouble() - 0.5) * 2,
                            player.getY() + world.random.nextDouble() * 2,
                            player.getZ() + (world.random.nextDouble() - 0.5) * 2);
        }
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
                            effect.shouldShowIcon()));
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
        ItemStack headStack = player.getInventory().getArmorStack(3);
        if (headStack.isEmpty() || !(headStack.getItem() instanceof ArmorItem))
            return null;

        ArmorItem first = (ArmorItem) headStack.getItem();
        ArmorMaterial type = first.getMaterial();

        for (ItemStack armor : player.getInventory().armor) {
            if (!(armor.getItem() instanceof ArmorItem armorItem) || armorItem.getMaterial() != type) {
                return null;
            }
        }
        return type;
    }
}
