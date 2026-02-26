package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NatureStaffItem extends Item {

    public NatureStaffItem(Settings settings) {
        super(settings);
    }

    // Logic for "Natural mobs defend them" requires Custom AI Goals or an Event
    // Handler.
    // Passive effect: AttackEntityCallback -> if attacker interacts with Holder?
    // Or TickHandler checking nearby animals and setting their target?
    // Let's rely on a TickHandler in ClassTickHandler later.

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Harmony:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Nature defends you.").formatted(Formatting.GREEN));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
