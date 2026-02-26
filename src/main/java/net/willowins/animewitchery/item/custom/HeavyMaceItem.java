package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeavyMaceItem extends SwordItem {

    public HeavyMaceItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        // Attack speed should be low (e.g., -3.0f or lower) passed in constructor
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Brute Force:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("High Damage and Knockback.").formatted(Formatting.DARK_RED));
        tooltip.add(Text.literal("Very Slow Attack Speed.").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }

    // Knockback logic is usually innate to high damage, but we can verify later.
    // Vanilla SwordItem attribute modifiers handle the "Slow" part if we pass
    // correct attackSpeed.
}
