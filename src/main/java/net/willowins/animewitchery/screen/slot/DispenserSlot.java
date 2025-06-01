package net.willowins.animewitchery.screen.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class DispenserSlot extends Slot {
    public DispenserSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        // You can add any logic here to restrict what can be inserted if needed,
        // or just accept any item like a normal dispenser slot.
        return true;
    }
}
