package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuiverItem extends ArmorItem {
    public QuiverItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Ammo Efficiency:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("50% chance to not consume arrows.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }

    // Logic for this is handled in ClassCombatHandler or Mixin (BowItem/Consumer)
}
