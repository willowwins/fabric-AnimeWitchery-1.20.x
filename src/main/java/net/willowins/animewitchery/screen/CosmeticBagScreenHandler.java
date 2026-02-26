package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class CosmeticBagScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public CosmeticBagScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(27));
    }

    public CosmeticBagScreenHandler(int syncId, PlayerInventory playerInventory,
            net.minecraft.network.PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(27));
    }

    public CosmeticBagScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(net.willowins.animewitchery.ModScreenHandlers.COSMETIC_BAG_HANDLER, syncId);
        checkSize(inventory, 27);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // Cosmetic Bag Slots (3 Rows of 9)
        // Rows 2 and 3 (Index 9-26) are standard storage
        // Row 1 (Index 0-3) are Vanity Slots
        // Row 1 (Index 4-8) are standard storage

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new RestrictedSlot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Player Inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Custom slot that only accepts armor items and glass
    private static class RestrictedSlot extends Slot {
        public RestrictedSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            // Allow glass
            if (stack.getItem() == net.minecraft.item.Items.GLASS) {
                return true;
            }
            // Allow any armor item
            if (stack.getItem() instanceof net.minecraft.item.ArmorItem) {
                return true;
            }
            return false;
        }
    }
}
