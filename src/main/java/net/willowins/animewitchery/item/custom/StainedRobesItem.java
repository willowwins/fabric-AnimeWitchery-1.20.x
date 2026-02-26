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

public class StainedRobesItem extends ArmorItem {

    public StainedRobesItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    // Logic handled in Mixin (LivingEntityMixin for eating/drinking OR
    // StatusEffectMixin)

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Chemical Mastery:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Doubles duration and strength of drank potions.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
