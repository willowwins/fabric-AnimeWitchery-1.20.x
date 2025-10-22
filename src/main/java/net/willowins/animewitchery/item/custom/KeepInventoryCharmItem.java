package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class KeepInventoryCharmItem extends TrinketItem {
    
    public KeepInventoryCharmItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Preserves inventory on death").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Consumed on use").formatted(Formatting.RED));
        tooltip.add(Text.literal("Must be worn in necklace slot").formatted(Formatting.GRAY));
    }
}

