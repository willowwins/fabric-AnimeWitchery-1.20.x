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
        // Check slots 1-10 for enchanted book + tool/book combination
        ItemStack firstBook = ItemStack.EMPTY;
        ItemStack secondItem = ItemStack.EMPTY;
        
        // Debug: Show what's in each slot
        System.out.println("DEBUG: Alchemy table slots:");
        for (int i = 0; i <= 10; i++) {
            ItemStack stack = alchemy.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isOf(Items.ENCHANTED_BOOK)) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack));
                    System.out.println("  Slot " + i + ": " + stack.getItem().toString() + " (enchanted book with: " + enchantments.keySet().stream().map(e -> e.getTranslationKey()).collect(java.util.stream.Collectors.joining(", ")) + ")");
                } else {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.fromNbt(stack.getEnchantments());
                    System.out.println("  Slot " + i + ": " + stack.getItem().toString() + " (tool with: " + enchantments.keySet().stream().map(e -> e.getTranslationKey()).collect(java.util.stream.Collectors.joining(", ")) + ")");
                }
            }
        }
        
        // Find first enchanted book and second item
        for (int i = 1; i <= 10; i++) {
            ItemStack stack = alchemy.getStack(i);
            if (stack.isEmpty()) continue;
            
            System.out.println("DEBUG: Processing slot " + i + ": " + stack.getItem().toString() + " (firstBook.isEmpty(): " + firstBook.isEmpty() + ", secondItem.isEmpty(): " + secondItem.isEmpty() + ")");
            
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                if (firstBook.isEmpty()) {
                    firstBook = stack;
                    System.out.println("DEBUG: Set firstBook from slot " + i);
                } else if (secondItem.isEmpty()) {
                    // Second enchanted book found
                    secondItem = stack;
                    System.out.println("DEBUG: Set secondItem (book) from slot " + i);
                }
            } else if (!stack.isOf(Items.BOOK) && secondItem.isEmpty() && !firstBook.isEmpty()) {
                // Any non-book item could be a tool/armor
                secondItem = stack;
                System.out.println("DEBUG: Set secondItem (tool) from slot " + i);
            } else {
                System.out.println("DEBUG: Skipped slot " + i + " - conditions not met");
            }
        }
        
        // Need exactly one book and one other item
        System.out.println("DEBUG: firstBook.isEmpty(): " + firstBook.isEmpty() + ", secondItem.isEmpty(): " + secondItem.isEmpty());
        if (firstBook.isEmpty() || secondItem.isEmpty()) {
            System.out.println("DEBUG: Missing book or item - returning false");
            return false;
        }
        
        // Count non-empty slots - should be exactly 2
        int nonEmptySlots = 0;
        for (int i = 1; i <= 10; i++) {
            if (!alchemy.getStack(i).isEmpty()) nonEmptySlots++;
        }
        System.out.println("DEBUG: nonEmptySlots count: " + nonEmptySlots + " (should be 2)");
        if (nonEmptySlots != 2) {
            System.out.println("DEBUG: Wrong number of non-empty slots - returning false");
            return false;
        }
        
        // Get enchantments from the first book
        Map<Enchantment, Integer> firstEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(firstBook));
        if (firstEnchantments.isEmpty()) return false;
        
        // Determine if we're combining books or applying to tool
        boolean isBookCombining = secondItem.isOf(Items.ENCHANTED_BOOK);
        
        if (isBookCombining) {
            // Book + Book combination - always valid if both are enchanted books
            return true;
        } else {
            // Book + Tool combination - check if any enchantment can be applied
            System.out.println("DEBUG: Checking book + tool combination:");
            System.out.println("  Book enchantments: " + firstEnchantments.keySet().stream().map(e -> e.getTranslationKey()).collect(java.util.stream.Collectors.joining(", ")));
            System.out.println("  Tool: " + secondItem.getItem().toString());
            
            for (Map.Entry<Enchantment, Integer> entry : firstEnchantments.entrySet()) {
                boolean canApply = entry.getKey().isAcceptableItem(secondItem);
                System.out.println("  " + entry.getKey().getTranslationKey() + " can apply to " + secondItem.getItem().toString() + ": " + canApply);
                if (canApply) {
                    return true;
                }
            }
            System.out.println("  No enchantments can be applied!");
            return false;
        }
    }

    private static boolean processEnchantmentCombining(AlchemyTableBlockEntity alchemy, World world) {
        
        // Check slots 1-10 for enchanted book + tool/book combination
        ItemStack firstBook = ItemStack.EMPTY;
        ItemStack secondItem = ItemStack.EMPTY;
        int firstSlot = -1;
        int secondSlot = -1;
        
        // Find first enchanted book and second item
        for (int i = 1; i <= 10; i++) {
            ItemStack stack = alchemy.getStack(i);
            if (stack.isEmpty()) continue;
            
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                if (firstBook.isEmpty()) {
                    firstBook = stack;
                    firstSlot = i;
                } else if (secondItem.isEmpty()) {
                    // Second enchanted book found
                    secondItem = stack;
                    secondSlot = i;
                }
            } else if (!stack.isOf(Items.BOOK) && secondItem.isEmpty() && !firstBook.isEmpty()) {
                // Any non-book item could be a tool/armor
                secondItem = stack;
                secondSlot = i;
            }
        }
        
        // Need exactly one book and one other item
        if (firstBook.isEmpty() || secondItem.isEmpty()) return false;
        
        // Count non-empty slots - should be exactly 2
        int nonEmptySlots = 0;
        for (int i = 1; i <= 10; i++) {
            if (!alchemy.getStack(i).isEmpty()) nonEmptySlots++;
        }
        System.out.println("DEBUG: nonEmptySlots count: " + nonEmptySlots + " (should be 2)");
        if (nonEmptySlots != 2) {
            System.out.println("DEBUG: Wrong number of non-empty slots - returning false");
            return false;
        }
        
        // Get enchantments from the first book
        Map<Enchantment, Integer> firstEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(firstBook));
        if (firstEnchantments.isEmpty()) return false;
        
        // Determine if we're combining books or applying to tool
        boolean isBookCombining = secondItem.isOf(Items.ENCHANTED_BOOK);
        
        if (isBookCombining) {
            // Book + Book combination
            Map<Enchantment, Integer> secondEnchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(secondItem));
            Map<Enchantment, Integer> combinedEnchantments = new java.util.HashMap<>(firstEnchantments);
            
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
            for (Map.Entry<Enchantment, Integer> entry : firstEnchantments.entrySet()) {
                if (entry.getKey().isAcceptableItem(secondItem)) {
                    canApply = true;
                    break;
                }
            }
            
            if (!canApply) return false;
            
            // Apply enchantments to tool
            Map<Enchantment, Integer> toolEnchantments = EnchantmentHelper.get(secondItem);
            
            for (Map.Entry<Enchantment, Integer> entry : firstEnchantments.entrySet()) {
                Enchantment ench = entry.getKey();
                int bookLevel = entry.getValue();
                
                if (!ench.isAcceptableItem(secondItem)) continue;
                
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
            ItemStack result = secondItem.copy();
            EnchantmentHelper.set(toolEnchantments, result);
            
            // Place result in output slot
            alchemy.setStack(0, result);
        }
        
        // Clear input slots
        alchemy.setStack(firstSlot, ItemStack.EMPTY);
        alchemy.setStack(secondSlot, ItemStack.EMPTY);
        
        // Sound is played by the main tick logic
        return true;
    }
}

