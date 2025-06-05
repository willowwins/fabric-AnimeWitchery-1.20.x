package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class ItemActionScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    // This constructor is called client & server side
    public ItemActionScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(type, syncId);
        this.inventory = inventory;

        // Replace your block inventory slots loop with:
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                int index = col + row * 3;
                int x = 62 + col * 18;  // dispenser GUI block slots start at x=62
                int y = 17 + row * 18;  // dispenser GUI block slots start at y=17
                this.addSlot(new Slot(inventory, index, x, y));
            }
        }


        // Add player inventory slots (3 rows of 9)
        int playerInvY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInvY + row * 18));
            }
        }

        // Add player hotbar slots (1 row of 9)
        int hotbarY = playerInvY + 58;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Quick move stack logic (shift-click)
    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            int inventorySize = this.inventory.size();

            if (index < inventorySize) {
                // Moving from block inventory to player inventory
                if (!this.insertItem(stack, inventorySize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to block inventory
                if (!this.insertItem(stack, 0, inventorySize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return result;
    }
}

