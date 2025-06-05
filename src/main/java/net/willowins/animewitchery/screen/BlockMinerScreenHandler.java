package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.entity.BlockMinerBlockEntity;

public class BlockMinerScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public BlockMinerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.BLOCK_MINER_SCREEN_HANDLER, syncId);
        this.inventory = inventory;

        // Add miner inventory slots (3 rows x 3 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // Add player inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Add hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack newStack = slot.getStack();
            ItemStack original = newStack.copy();

            if (index < 9) {
                if (!this.insertItem(newStack, 9, 45, true)) return ItemStack.EMPTY;
            } else if (!this.insertItem(newStack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (newStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            return original;
        }

        return ItemStack.EMPTY;
    }
}
