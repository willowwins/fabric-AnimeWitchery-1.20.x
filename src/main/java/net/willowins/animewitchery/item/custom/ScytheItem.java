package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class ScytheItem extends SwordItem {
    public ScytheItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            // Lifesteal: 10% of max health or flat amount?
            // Let's do 1 heart (2.0f) per hit.
            player.heal(2.0f);
        }
        return super.postHit(stack, target, attacker);
    }
}
