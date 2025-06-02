package net.willowins.animewitchery.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ItemStackHelper {
    public static ItemStack splitStack(DefaultedList<ItemStack> stacks, int slot, int amount) {
        ItemStack stack = stacks.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        if (stack.getCount() <= amount) {
            stacks.set(slot, ItemStack.EMPTY);
            return stack;
        } else {
            ItemStack result = stack.split(amount);
            if (stack.isEmpty()) {
                stacks.set(slot, ItemStack.EMPTY);
            }
            return result;
        }
    }

    public static ItemStack removeStack(DefaultedList<ItemStack> stacks, int slot) {
        ItemStack stack = stacks.get(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        stacks.set(slot, ItemStack.EMPTY);
        return stack;
    }
}
