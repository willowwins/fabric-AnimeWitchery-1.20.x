package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.ModScreenHandlers;

public class BlockPlacerScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    // Server + Client constructor
    public BlockPlacerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.BLOCK_PLACER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 9);
        this.inventory = inventory;

        // BlockPlacer inventory 3x3 slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(inventory, col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }

        // Player inventory slots (3 rows x 9 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    // Client constructor, called by ScreenHandlerRegistry (reads block pos from buf)
    public BlockPlacerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(9)); // Client uses empty inventory (sync happens later)
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack original = stack.copy();

            if (index < 9) {
                if (!insertItem(stack, 9, 45, true)) return ItemStack.EMPTY;
            } else if (!insertItem(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            return original;
        }
        return ItemStack.EMPTY;
    }
}
