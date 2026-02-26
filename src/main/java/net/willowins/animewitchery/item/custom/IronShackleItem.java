package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronShackleItem extends SwordItem {

    public IronShackleItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    // Ideally this behaves like a "Fist" weapon.
    // It should probably have very low base damage but high attack speed?
    // User said "can upgrade unarmed damage".
    // Maybe holding it counts as "Unarmed" for perks?

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Martial Arts:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Significantly boosts unarmed combat.").formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
