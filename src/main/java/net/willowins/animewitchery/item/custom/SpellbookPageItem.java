package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A physical page that stores a single spell entry (one configured spell)
 * Used in the spellbook's internal inventory to represent each spell
 */
public class SpellbookPageItem extends Item {
    
    private static final String SPELL_ENTRY_KEY = "spell_entry";
    
    public SpellbookPageItem(Settings settings) {
        super(settings);
    }
    
    /**
     * Creates a spellbook page from a SpellEntry
     */
    public static ItemStack createPage(SpellEntry spellEntry) {
        ItemStack page = new ItemStack(net.willowins.animewitchery.item.ModItems.SPELLBOOK_PAGE);
        NbtCompound nbt = page.getOrCreateNbt();
        
        // Store the spell entry
        nbt.put(SPELL_ENTRY_KEY, spellEntry.toNbt());
        
        return page;
    }
    
    /**
     * Gets the SpellEntry from this page
     */
    public static SpellEntry getSpellEntry(ItemStack page) {
        NbtCompound nbt = page.getNbt();
        if (nbt == null || !nbt.contains(SPELL_ENTRY_KEY)) {
            return null;
        }
        
        return SpellEntry.fromNbt(nbt.getCompound(SPELL_ENTRY_KEY));
    }
    
    /**
     * Checks if this page has a spell entry
     */
    public static boolean hasSpellEntry(ItemStack page) {
        return getSpellEntry(page) != null;
    }
    
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        SpellEntry entry = getSpellEntry(stack);
        
        if (entry != null) {
            tooltip.add(Text.literal("Spell: " + entry.getSpellName()).formatted(Formatting.GOLD));
            tooltip.add(Text.literal("Position: " + entry.getPosition().getDisplayName()).formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Target: " + entry.getTargeting().getDisplayName()).formatted(Formatting.AQUA));
            tooltip.add(Text.literal("Delay: " + entry.getDelay() + "ms").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Multiplicity: x" + entry.getMultiplicity()).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("Empty Page").formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
        }
        
        super.appendTooltip(stack, world, tooltip, context);
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasSpellEntry(stack);
    }
    
    @Override
    public Text getName(ItemStack stack) {
        SpellEntry entry = getSpellEntry(stack);
        if (entry != null) {
            return Text.literal("Spell Page: " + entry.getSpellName()).formatted(Formatting.LIGHT_PURPLE);
        }
        return super.getName(stack);
    }
}

