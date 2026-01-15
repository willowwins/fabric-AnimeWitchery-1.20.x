package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class HaloicSwordItem extends SwordItem {
    public HaloicSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.getGroup() == EntityGroup.UNDEAD) {
            // "Holy Smite" effect: Deal extra damage to Undead
            target.damage(attacker.getDamageSources().magic(), 10.0f); // 5 Hearts extra damage
            target.setOnFireFor(5); // Burn them
        }
        return super.postHit(stack, target, attacker);
    }
}
