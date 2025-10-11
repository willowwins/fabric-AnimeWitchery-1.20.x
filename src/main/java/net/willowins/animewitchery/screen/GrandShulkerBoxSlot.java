package net.willowins.animewitchery.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class GrandShulkerBoxSlot extends Slot {
    public GrandShulkerBoxSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxItemCount(ItemStack stack) {
        return stack.getMaxCount(); // Use the item's default max stack size
    }
}
