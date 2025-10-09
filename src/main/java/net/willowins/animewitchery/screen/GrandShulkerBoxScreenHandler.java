package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;

public class GrandShulkerBoxScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final GrandShulkerBoxBlockEntity blockEntity;

    public GrandShulkerBoxScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, GrandShulkerBoxBlockEntity blockEntity) {
        super(type, syncId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity; // Use the block entity directly as inventory

        // Add container slots (6 rows of 9 = 54 slots) with custom stack size
        for (int row = 0; row < 6; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new GrandShulkerBoxSlot(inventory, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }

        // Add player inventory slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Add player hotbar slots
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < 54) {
                if (!this.insertItem(itemStack2, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 54, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // Mark the block entity as dirty to ensure changes are saved
        if (blockEntity != null) {
            blockEntity.markDirty();
            // Trigger close animation
            blockEntity.setOpen(false);
            blockEntity.triggerAnim("controller", "close");
        }
    }
}
