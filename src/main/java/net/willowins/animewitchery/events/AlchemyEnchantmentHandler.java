package net.willowins.animewitchery.events;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;

import java.util.Map;

/**
 * Allows combining enchanted books with tools/armor in the alchemy table
 * Works like an anvil but through alchemy
 */
public class AlchemyEnchantmentHandler {

    public static void register() {
        // No registration needed - called directly from AlchemyTableBlockEntity
    }
    
    /**
     * Called from AlchemyTableBlockEntity when activated
     */
    public static boolean tryProcessEnchantmentCombining(AlchemyTableBlockEntity alchemy, World world) {
        if (world.isClient) return false;
        return processEnchantmentCombining(alchemy, world);
    }
    
    /**
     * Check if enchantment combining is possible without actually doing it
     */
    public static boolean canCombineEnchantments(AlchemyTableBlockEntity alchemy) {
        // Only check for enchantment combining if the first slot contains a weapon/tool or enchanted book
        ItemStack firstSlot = alchemy.getStack(1);
        if (firstSlot.isEmpty()) {
            return false;
        }
        
        boolean isEnchantableItem = firstSlot.getItem().getEnchantability() > 0 || 
                                   firstSlot.getItem() instanceof net.minecraft.item.ArmorItem || 
                                   firstSlot.getItem() instanceof net.minecraft.item.ToolItem || 
                                   firstSlot.getItem() instanceof net.minecraft.item.SwordItem ||
                                   firstSlot.isOf(Items.ENCHANTED_BOOK);
        
        // If first slot is not an enchantable item or enchanted book, skip enchantment check
        if (!isEnchantableItem) {
            return false;
        }
        
        // Now look for an enchanted book (if first slot is a tool) or a tool (if first slot is a book)
        ItemStack enchantedBook = ItemStack.EMPTY;
        ItemStack recipientItem = ItemStack.EMPTY;
        
        if (firstSlot.isOf(Items.ENCHANTED_BOOK)) {
            enchantedBook = firstSlot;
        } else {
            recipientItem = firstSlot;
        }
        
        // Find the missing piece
        for (int i = 2; i <= 10; i++) {
            ItemStack stack = alchemy.getStack(i);
            if (stack.isEmpty()) continue;
            
            if (stack.isOf(Items.ENCHANTED_BOOK) && enchantedBook.isEmpty()) {
                enchantedBook = stack;
            } else if (!stack.isOf(Items.ENCHANTED_BOOK) && !stack.isOf(Items.BOOK) && recipientItem.isEmpty()) {
                // Check if this item can be enchanted
                if (stack.getItem().getEnchantability() > 0 || stack.getItem() instanceof net.minecraft.item.ArmorItem || 
                    stack.getItem() instanceof net.minecraft.item.ToolItem || stack.getItem() instanceof net.minecraft.item.SwordItem) {
                    recipientItem = stack;
                }
            }
        }
        
        // Need both enchanted book and recipient item
        if (enchantedBook.isEmpty() || recipientItem.isEmpty()) {
            return false;
        }
        
        // Count non-empty slots - should be exactly 2
        int nonEmptySlots = 0;
        for (int i = 1; i <= 10; i++) {
            if (!alchemy.getStack(i).isEmpty()) nonEmptySlots++;
        }
        
        if (nonEmptySlots != 2) {
            return false;
        }
        
        // Get enchantments from the enchanted book
        Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(enchantedBook));
        if (bookEnchantments.isEmpty()) return false;
        
        // Determine if we're combining books or applying to tool
        boolean isBookCombining = recipientItem.isOf(Items.ENCHANTED_BOOK);
        
        if (isBookCombining) {
            // Book + Book combination - always valid if both are enchanted books
            return true;
        } else {
            // Book + Tool combination - check if any enchantment can be applied
            for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                boolean canApply = entry.getKey().isAcceptableItem(recipientItem);
                if (canApply) {
                    return true;
                }
            }
            return false;
        }
    }

    private static boolean processEnchantmentCombining(AlchemyTableBlockEntity alchemy, World world) {
        
        // Only process if the first slot contains a weapon/tool or enchanted book
        ItemStack firstSlot = alchemy.getStack(1);
        if (firstSlot.isEmpty()) {
            return false;
        }
        
        boolean isEnchantableItem = firstSlot.getItem().getEnchantability() > 0 || 
                                   firstSlot.getItem() instanceof net.minecraft.item.ArmorItem || 
                                   firstSlot.getItem() instanceof net.minecraft.item.ToolItem || 
                                   firstSlot.getItem() instanceof net.minecraft.item.SwordItem ||
                                   firstSlot.isOf(Items.ENCHANTED_BOOK);
        
        // If first slot is not an enchantable item or enchanted book, skip
        if (!isEnchantableItem) {
            return false;
        }
        
        // Now look for an enchanted book (if first slot is a tool) or a tool (if first slot is a book)
        ItemStack enchantedBook = ItemStack.EMPTY;
        ItemStack recipientItem = ItemStack.EMPTY;
        int bookSlot = -1;
        int itemSlot = -1;
        
        if (firstSlot.isOf(Items.ENCHANTED_BOOK)) {
            enchantedBook = firstSlot;
            bookSlot = 1;
        } else {
            recipientItem = firstSlot;
            itemSlot = 1;
        }
        
        // Find the missing piece
        for (int i = 2; i <= 10; i++) {
            ItemStack stack = alchemy.getStack(i);
            if (stack.isEmpty()) continue;
            
            if (stack.isOf(Items.ENCHANTED_BOOK) && enchantedBook.isEmpty()) {
                enchantedBook = stack;
                bookSlot = i;
            } else if (!stack.isOf(Items.ENCHANTED_BOOK) && !stack.isOf(Items.BOOK) && recipientItem.isEmpty()) {
                // Check if this item can be enchanted
                if (stack.getItem().getEnchantability() > 0 || stack.getItem() instanceof net.minecraft.item.ArmorItem || 
                    stack.getItem() instanceof net.minecraft.item.ToolItem || stack.getItem() instanceof net.minecraft.item.SwordItem) {
                    recipientItem = stack;
                    itemSlot = i;
                }
            }
        }
        
        // Need both enchanted book and recipient item
        if (enchantedBook.isEmpty() || recipientItem.isEmpty()) {
            return false;
        }
        
        // Get enchantments from the enchanted book
        Map<Enchantment, Integer> bookEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(enchantedBook));
        if (bookEnchantments.isEmpty()) return false;
        
        // Determine if we're combining books or applying to tool
        boolean isBookCombining = recipientItem.isOf(Items.ENCHANTED_BOOK);
        
        if (isBookCombining) {
            // Book + Book combination
            Map<Enchantment, Integer> secondEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(recipientItem));
            Map<Enchantment, Integer> combinedEnchantments = new java.util.HashMap<>(bookEnchantments);
            
            // Combine enchantments from both books
            for (Map.Entry<Enchantment, Integer> entry : secondEnchantments.entrySet()) {
                Enchantment ench = entry.getKey();
                int secondLevel = entry.getValue();
                int firstLevel = combinedEnchantments.getOrDefault(ench, 0);
                int newLevel = firstLevel;
                
                if (firstLevel == secondLevel && firstLevel < ench.getMaxLevel()) {
                    // Same level - increase by 1
                    newLevel = firstLevel + 1;
                } else if (secondLevel > firstLevel) {
                    // Higher level wins
                    newLevel = secondLevel;
                }
                
                combinedEnchantments.put(ench, newLevel);
            }
            
            // Create enchanted book result
            ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.set(combinedEnchantments, result);
            
            // Place result in output slot
            alchemy.setStack(0, result);
        } else {
            // Book + Tool combination
            // Check if any enchantment from the book can be applied to the tool
            boolean canApply = false;
            for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                if (entry.getKey().isAcceptableItem(recipientItem)) {
                    canApply = true;
                    break;
                }
            }
            
            if (!canApply) return false;
            
            // Apply enchantments to tool
            Map<Enchantment, Integer> toolEnchantments = EnchantmentHelper.get(recipientItem);
            
            for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
                Enchantment ench = entry.getKey();
                int bookLevel = entry.getValue();
                
                if (!ench.isAcceptableItem(recipientItem)) continue;
                
                // Combine with existing level
                int existingLevel = toolEnchantments.getOrDefault(ench, 0);
                int newLevel = existingLevel;
                
                // If same level, increase by 1 (up to max)
                if (bookLevel == existingLevel && bookLevel < ench.getMaxLevel()) {
                    newLevel = bookLevel + 1;
                } else if (bookLevel > existingLevel) {
                    newLevel = bookLevel;
                }
                
                toolEnchantments.put(ench, newLevel);
            }
            
            // Create the result
            ItemStack result = recipientItem.copy();
            EnchantmentHelper.set(toolEnchantments, result);
            
            // Place result in output slot
            alchemy.setStack(0, result);
        }
        
        // Clear input slots
        alchemy.setStack(bookSlot, ItemStack.EMPTY);
        alchemy.setStack(itemSlot, ItemStack.EMPTY);
        
        // Sound is played by the main tick logic
        return true;
    }
}

