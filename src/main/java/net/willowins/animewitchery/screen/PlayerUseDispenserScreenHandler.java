package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.screen.slot.DispenserSlot;
import net.willowins.animewitchery.ModScreenHandlers;


public class PlayerUseDispenserScreenHandler extends ScreenHandler {
    private final SidedInventory inventory;

    public PlayerUseDispenserScreenHandler(int syncId, PlayerInventory playerInventory, SidedInventory inventory) {
        super(ModScreenHandlers.PLAYER_USE_DISPENSER_SCREEN_HANDLER, syncId);
        this.inventory = inventory;

        checkSize(inventory, 9);
        inventory.onOpen(playerInventory.player);

        // Add dispenser inventory slots (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new DispenserSlot(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // Player inventory slots (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar slots (1x9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack stackInSlot = slot.getStack();
            originalStack = stackInSlot.copy();

            int inventorySize = this.inventory.size();
            int playerInventoryStart = inventorySize;
            int playerInventoryEnd = playerInventoryStart + 27;  // 3 rows * 9 cols
            int hotbarStart = playerInventoryEnd;
            int hotbarEnd = hotbarStart + 9;

            if (index < inventorySize) {
                // From dispenser inventory to player inventory
                if (!this.insertItem(stackInSlot, playerInventoryStart, hotbarEnd, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to dispenser inventory
                if (!this.insertItem(stackInSlot, 0, inventorySize, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (stackInSlot.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, stackInSlot);
        }

        return originalStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }
}