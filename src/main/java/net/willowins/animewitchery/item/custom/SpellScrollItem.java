package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Spell scroll item that contains a spell name
 * Can be crafted through alchemy and used to encode wands
 */
public class SpellScrollItem extends Item {
    
    private final String spellName;
    
    public SpellScrollItem(Settings settings, String spellName) {
        super(settings);
        this.spellName = spellName;
    }
    
    public String getSpellName() {
        return spellName;
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.animewitchery.spell_scroll", spellName));
        tooltip.add(Text.translatable("tooltip.animewitchery.spell_scroll_use"));
        super.appendTooltip(stack, world, tooltip, context);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // All spell scrolls have enchantment glint
    }
    
    @Override
    public Text getName(ItemStack stack) {
        Formatting color = getSpellColor(spellName);
        return Text.translatable(this.getTranslationKey(stack)).formatted(color);
    }
    
    /**
     * Gets the color formatting for a spell type
     */
    private static Formatting getSpellColor(String spellName) {
        return switch (spellName) {
            case "Fire Blast" -> Formatting.RED;
            case "Water Shield" -> Formatting.AQUA;
            case "Earth Spike" -> Formatting.DARK_GREEN;
            case "Wind Gust" -> Formatting.WHITE;
            case "Healing Wave" -> Formatting.GREEN;
            case "Wither Touch" -> Formatting.DARK_PURPLE;
            case "Light Burst" -> Formatting.YELLOW;
            case "Shadow Bind" -> Formatting.DARK_GRAY;
            default -> Formatting.RESET;
        };
    }
}
