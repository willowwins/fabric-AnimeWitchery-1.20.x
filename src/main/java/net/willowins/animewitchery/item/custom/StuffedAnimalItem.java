package net.willowins.animewitchery.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class StuffedAnimalItem extends SwordItem {
    public StuffedAnimalItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
            // Zero damage (or very low). Vanilla hand is 1.0.
            // If we want "Unable to do damage", we might need negative modifier?
            // "Unable to do damage" -> 0 actual damage?
            // Vanilla Punch is 1 dmg.
            // Let's set modifier to -1.0 so total is 0?
            // Or just very low.
            // User said "Unable to do damage".
            // Let's try to cancel damage event if holding it? Or set Attributes to 0.

            builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID,
                    "Weapon modifier", 0.0, EntityAttributeModifier.Operation.ADDITION));
            builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID,
                    "Weapon modifier", -2.0, EntityAttributeModifier.Operation.ADDITION)); // Fast swing? "Fast and
                                                                                           // cute".

            return builder.build();
        }
        return super.getAttributeModifiers(slot);
    }
}
