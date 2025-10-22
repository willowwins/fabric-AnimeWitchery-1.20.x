package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class KeepInventoryCharmItem extends Item {
    
    public KeepInventoryCharmItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Preserves inventory on death").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Consumed on use").formatted(Formatting.RED));
        tooltip.add(Text.literal("Must be in inventory").formatted(Formatting.GRAY));
    }
}

