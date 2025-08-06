package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;

public class AlchemyTableScreenHandler extends ScreenHandler {
    private final AlchemyTableBlockEntity blockEntity;
    private final PlayerInventory playerInventory;
    private final Inventory inventory;

    public AlchemyTableScreenHandler(int syncId, PlayerInventory playerInventory, AlchemyTableBlockEntity blockEntity) {
        super(ModScreenHandlers.ALCHEMY_TABLE_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
        this.playerInventory = playerInventory;
        this.inventory = blockEntity;

        // Add property delegate for syncing progress
        this.addProperties(blockEntity.getPropertyDelegate());

        // Add alchemy table slots in circular layout
        // Center: Output slot (slot 0)
        this.addSlot(new Slot(inventory, 0, 79, 71) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output slot is read-only
            }
        });
        
        // 10 input slots arranged counter-clockwise around the center
        this.addSlot(new Slot(inventory, 1, 56, 10));
        this.addSlot(new Slot(inventory, 2, 29, 36));
        this.addSlot(new Slot(inventory, 3, 21, 72));
        this.addSlot(new Slot(inventory, 4, 29, 107));
        this.addSlot(new Slot(inventory, 5, 56, 133)); 
        this.addSlot(new Slot(inventory, 6, 103, 133));
        this.addSlot(new Slot(inventory, 7, 130, 108));
        this.addSlot(new Slot(inventory, 8, 138, 72));
        this.addSlot(new Slot(inventory, 9, 130, 36));
        this.addSlot(new Slot(inventory, 10, 103, 10)); 

        // Add player inventory slots (3x9 grid) - slots 9-35
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slot = row * 9 + col + 9; // Start from slot 9 (main inventory)
                int x = 8 + col * 18;
                int y = 170 + row * 18; // Much lower to align with texture
                this.addSlot(new Slot(playerInventory, slot, x, y));
            }
        }

        // Add player hotbar slots - slots 0-8
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 228; // Much lower to align with texture
            this.addSlot(new Slot(playerInventory, col, x, y));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < 11) {
                // Moving from alchemy table to player inventory
                if (!this.insertItem(originalStack, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from player inventory to alchemy table
                if (!this.insertItem(originalStack, 1, 11, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    public PropertyDelegate getPropertyDelegate() {
        return blockEntity.getPropertyDelegate();
    }
    
    public int getCurrentRecipeXpCost() {
        return blockEntity.getCurrentRecipeXpCost();
    }
} 