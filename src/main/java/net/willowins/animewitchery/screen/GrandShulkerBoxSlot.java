package net.willowins.animewitchery.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.block.ModBlocks;

public class GrandShulkerBoxSlot extends Slot {
    public GrandShulkerBoxSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        // Allow 8x the normal stack size for all items
        return stack.getMaxCount() * 8;
    }
    
    @Override
    public int getMaxItemCount() {
        // Return the maximum possible stack size (8 * 64 = 512)
        return 512;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        // Check if the item is a grand shulker box
        if (stack.isOf(ModBlocks.GRAND_SHULKER_BOX.asItem())) {
            // Only allow enchanted grand shulker boxes to be placed inside
            return !stack.getEnchantments().isEmpty();
        }
        
        // Allow all other items
        return super.canInsert(stack);
    }
    
    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        // Get the current stack in this slot
        ItemStack currentStack = this.getStack();
        
        if (currentStack.isEmpty()) {
            // Slot is empty, insert the stack
            int maxCount = this.getMaxItemCount(stack);
            int insertCount = Math.min(count, maxCount);
            ItemStack newStack = stack.copy();
            newStack.setCount(insertCount);
            this.setStack(newStack);
            stack.decrement(insertCount);
            this.markDirty();
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        } else if (ItemStack.canCombine(currentStack, stack)) {
            // Stacks can be combined
            int maxCount = this.getMaxItemCount(stack);
            int availableSpace = maxCount - currentStack.getCount();
            
            if (availableSpace <= 0) {
                // Stack is already at max, refuse insertion
                return stack;
            }
            
            int insertCount = Math.min(count, availableSpace);
            currentStack.increment(insertCount);
            stack.decrement(insertCount);
            this.markDirty();
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }
        
        // Can't insert
        return stack;
    }
}
