package net.willowins.animewitchery.screen;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.GrowthAcceleratorBlockEntity;

public class GrowthAcceleratorScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private static final int INVENTORY_ROWS = 6;      // 6 rows for double chest
    private static final int INVENTORY_COLUMNS = 9;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * INVENTORY_COLUMNS; // 54 slots

    public GrowthAcceleratorScreenHandler(int syncId, PlayerInventory playerInventory, GrowthAcceleratorBlockEntity blockEntity) {
        this(syncId, playerInventory, blockEntity, blockEntity.getPropertyDelegate());

    }

    public GrowthAcceleratorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.GROWTH_ACCELERATOR_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        checkSize(inventory, INVENTORY_SIZE);

        // Add block entity slots (6 rows x 9 columns), like a double chest inventory
        for (int row = 0; row < INVENTORY_ROWS; ++row) {
            for (int col = 0; col < INVENTORY_COLUMNS; ++col) {
                this.addSlot(new Slot(inventory, col + row * INVENTORY_COLUMNS, 8 + col * 18, 18 + row * 18));
            }
        }

        // Add player inventory slots (3 rows x 9 columns)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        // Add player hotbar slots (1 row x 9 columns)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }

        addProperties(propertyDelegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // Block inventory slots are indices 0 to 53
            if (index < INVENTORY_SIZE) {
                // Move from block inventory to player inventory/hotbar
                if (!this.insertItem(originalStack, INVENTORY_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory/hotbar to block inventory
                if (!this.insertItem(originalStack, 0, INVENTORY_SIZE, false)) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Fuel slot restriction class (optional)
    private static class FuelSlot extends Slot {
        public FuelSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return isFuel(stack);
        }

        public static boolean isFuel(ItemStack stack) {
            return !stack.isEmpty() && (
                    FuelRegistry.INSTANCE.get(stack.getItem()) != null ||
                            stack.isOf(ModBlocks.CHARCOAL_BLOCK.asItem())
            );
        }
    }

    public static boolean isFuel(ItemStack stack) {
        return FuelSlot.isFuel(stack);
    }
}
