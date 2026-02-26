package net.willowins.animewitchery.item.custom;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.item.ModItems;

import java.util.List;

public class LatentManaPendantItem extends TrinketItem {
    private static final int BONUS_MANA = 20000;

    public LatentManaPendantItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Expands latent mana pool").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("+20,000 Maximum Mana").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Must be worn in necklace slot").formatted(Formatting.GRAY));
    }

    // Get the bonus mana amount for external access
    public static int getBonusMana(LivingEntity entity) {
        // This will be checked by the mana system
        return BONUS_MANA;
    }
}
