package net.willowins.animewitchery.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.WandItem;

/**
 * Utility class for encoding spells into wands using rune stones
 */
public class SpellEncoder {
    
    /**
     * Attempts to encode a spell into a wand using a spell scroll
     * @param player The player using the wand
     * @param world The world
     * @param hand The hand holding the wand
     * @param wandStack The wand item stack
     * @param scrollStack The spell scroll item stack
     * @return ActionResult indicating success or failure
     */
    public static ActionResult encodeSpellFromRune(PlayerEntity player, World world, Hand hand, 
                                                   ItemStack wandStack, ItemStack scrollStack) {
        if (world.isClient) {
            return ActionResult.PASS;
        }
        
        // Determine spell type from scroll
        String spellName = getSpellFromScroll(scrollStack);
        if (spellName == null) {
            return ActionResult.PASS;
        }
        
        // Check if wand already has a spell
        if (WandItem.hasSpell(wandStack)) {
            player.sendMessage(Text.translatable("message.animewitchery.wand_already_encoded"), true);
            return ActionResult.FAIL;
        }
        
        // Encode the spell
        WandItem.encodeSpell(wandStack, spellName);
        WandItem.setWandPower(wandStack, 10); // Base power level
        
        // Consume the scroll
        scrollStack.decrement(1);
        
        player.sendMessage(Text.translatable("message.animewitchery.spell_encoded", spellName), true);
        
        return ActionResult.SUCCESS;
    }
    
    /**
     * Gets the spell name from a spell scroll item
     */
    private static String getSpellFromScroll(ItemStack scrollStack) {
        if (scrollStack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellScrollItem scrollItem) {
            return scrollItem.getSpellName();
        }
        return null;
    }
    
    /**
     * Checks if an item is a spell scroll
     */
    public static boolean isRuneStone(ItemStack stack) {
        return stack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellScrollItem;
    }
}
