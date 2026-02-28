package net.willowins.animewitchery.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public class ScytheItem extends MiningToolItem {
    public ScytheItem(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, toolMaterial, BlockTags.HOE_MINEABLE, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            // Lifesteal: 1 heart (2.0f) per hit.
            player.heal(2.0f);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return state.isIn(BlockTags.HOE_MINEABLE) ? this.miningSpeed : 1.0F;
    }
}
