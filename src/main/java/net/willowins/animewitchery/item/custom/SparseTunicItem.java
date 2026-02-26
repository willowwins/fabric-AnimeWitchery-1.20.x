package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SparseTunicItem extends ArmorItem {

    public SparseTunicItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    // Hunger reduction needs to be handled via Mixin to HungerManager or
    // EntityExhaustionEvent.
    // Can't easily do it in tick().
    // We will assume "ClassArmorHandler" or Mixin handles the logic later.
    // But we can add the tooltip here.

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Asceticism:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Hunger drains very slowly.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
