package net.willowins.animewitchery.events;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.item.custom.SpellbookItem;
import net.willowins.animewitchery.item.custom.SpellScrollItem;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellEntry;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellPosition;
import net.willowins.animewitchery.util.AdvancedSpellConfiguration.SpellTargeting;

import java.util.Random;

/**
 * Handles spellbook + spell scroll combining in the alchemy table
 */
public class AlchemySpellbookHandler {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Try to process spellbook + spell scroll combination
     */
    public static boolean tryProcessSpellbookCombining(AlchemyTableBlockEntity entity, World world) {
        // Check if there's a spellbook and spell scroll combination
        if (!canCombineSpellScroll(entity)) {
            return false;
        }
        
        return processSpellbookCombining(entity, world);
    }
    
    /**
     * Check if the items can be combined (1 spellbook + 1 spell scroll)
     */
    public static boolean canCombineSpellScroll(AlchemyTableBlockEntity entity) {
        ItemStack spellbook = null;
        ItemStack spellScroll = null;
        int itemCount = 0;
        
        // Check all slots for spellbook and spell scroll
        for (int i = 0; i < entity.size(); i++) {
            ItemStack stack = entity.getStack(i);
            if (!stack.isEmpty()) {
                itemCount++;
                
                if (stack.getItem() instanceof SpellbookItem) {
                    if (spellbook != null) return false; // More than one spellbook
                    spellbook = stack;
                } else if (stack.getItem() instanceof SpellScrollItem) {
                    if (spellScroll != null) return false; // More than one spell scroll
                    spellScroll = stack;
                }
            }
        }
        
        // Must have exactly 1 spellbook and 1 spell scroll (2 items total)
        return itemCount == 2 && spellbook != null && spellScroll != null;
    }
    
    /**
     * Process the spellbook + spell scroll combination
     */
    private static boolean processSpellbookCombining(AlchemyTableBlockEntity entity, World world) {
        ItemStack spellbook = null;
        ItemStack spellScroll = null;
        int spellbookSlot = -1;
        int scrollSlot = -1;
        
        // Find the items
        for (int i = 0; i < entity.size(); i++) {
            ItemStack stack = entity.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof SpellbookItem) {
                    spellbook = stack;
                    spellbookSlot = i;
                } else if (stack.getItem() instanceof SpellScrollItem) {
                    spellScroll = stack;
                    scrollSlot = i;
                }
            }
        }
        
        if (spellbook == null || spellScroll == null) {
            return false;
        }
        
        // Get the spell name from the scroll
        SpellScrollItem scrollItem = (SpellScrollItem) spellScroll.getItem();
        String spellName = scrollItem.getSpellName();
        
        // Get or create advanced configuration
        AdvancedSpellConfiguration config = SpellbookItem.getAdvancedConfiguration(spellbook);
        if (config == null) {
            config = new AdvancedSpellConfiguration("Advanced Spells");
        }
        
        // Create new spell entry with random properties
        SpellEntry newSpell = new SpellEntry(
            spellName,
            SpellPosition.values()[RANDOM.nextInt(SpellPosition.values().length)],
            SpellTargeting.values()[RANDOM.nextInt(SpellTargeting.values().length)],
            new int[]{0, 100, 250, 500, 750, 1000, 1250, 1500, 1750}[RANDOM.nextInt(9)],
            RANDOM.nextInt(5) + 1, // 1-5 multiplicity
            net.minecraft.util.math.Vec3d.ZERO, // offset
            0.0f, // yawOffset
            0.0f  // pitchOffset
        );
        
        // Add spell to configuration
        config.addSpell(newSpell);
        
        // Create result spellbook with updated configuration
        ItemStack resultBook = spellbook.copy();
        SpellbookItem.saveAdvancedConfiguration(resultBook, config);
        
        // Clear input items
        entity.setStack(spellbookSlot, ItemStack.EMPTY);
        entity.setStack(scrollSlot, ItemStack.EMPTY);
        
        // Set result in output slot (slot 10)
        entity.setStack(10, resultBook);
        
        // Mark the entity as dirty so it saves
        entity.markDirty();
        
        return true;
    }
}


