package net.willowins.animewitchery.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.block.entity.AutoCrafterBlockEntity;

public class AutoCrafterScreenHandler extends ScreenHandler {

    private final AutoCrafterBlockEntity blockEntity;
    private final PlayerInventory playerInventory;
    private final Inventory recipeInventory;

    public AutoCrafterScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, AutoCrafterBlockEntity blockEntity) {
        super(type, syncId);
        this.blockEntity = blockEntity;
        this.playerInventory = playerInventory;
        this.recipeInventory = new Inventory() { // Expose just the recipeInventory part of blockEntity
            @Override
            public int size() {
                return 9;
            }

            @Override
            public boolean isEmpty() {
                for (int i = 0; i < size(); i++) {
                    if (!blockEntity.getRecipeInventory().get(i).isEmpty()) return false;
                }
                return true;
            }

            @Override
            public ItemStack getStack(int slot) {
                return blockEntity.getRecipeInventory().get(slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                ItemStack stack = blockEntity.getRecipeInventory().get(slot);
                if (stack.isEmpty()) return ItemStack.EMPTY;
                ItemStack result;
                if (stack.getCount() <= amount) {
                    result = stack;
                    blockEntity.getRecipeInventory().set(slot, ItemStack.EMPTY);
                } else {
                    result = stack.split(amount);
                }
                blockEntity.markDirty();
                return result;
            }

            @Override
            public ItemStack removeStack(int slot) {
                ItemStack result = blockEntity.getRecipeInventory().get(slot);
                blockEntity.getRecipeInventory().set(slot, ItemStack.EMPTY);
                blockEntity.markDirty();
                return result;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                blockEntity.getRecipeInventory().set(slot, stack);
                blockEntity.markDirty();
            }

            @Override
            public void markDirty() {
                blockEntity.markDirty();
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return blockEntity.canPlayerUse(player);
            }

            @Override
            public void clear() {
                for (int i = 0; i < size(); i++) {
                    blockEntity.getRecipeInventory().set(i, ItemStack.EMPTY);
                }
                blockEntity.markDirty();
            }
        };

        // Add 3x3 recipe input grid (slots 0-8)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slot = row * 3 + col;
                int x = 30 + col * 18;
                int y = 17 + row * 18;
                this.addSlot(new Slot(recipeInventory, slot, x, y));
            }
        }

        // Player inventory (3 rows x 9 columns)
        int playerInvY = 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInvY + row * 18));
            }
        }

        // Player hotbar (1 row x 9 columns)
        int hotbarY = 142;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            int containerSlots = 9; // Recipe grid slots count

            if (invSlot < containerSlots) {
                // Move from recipe grid to player inventory
                if (!this.insertItem(originalStack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory to recipe grid
                if (!this.insertItem(originalStack, 0, containerSlots, false)) {
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
}
