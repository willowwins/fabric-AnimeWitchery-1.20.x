package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ButcherKnifeItem extends SwordItem {

    public ButcherKnifeItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Apply Bleeding (5s = 100 ticks)
        target.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 100, 0));
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Butcher's Trade:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Causes Bleeding on hit.").formatted(Formatting.RED));
        tooltip.add(Text.literal("Prey drops extra meat.").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
