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
                // From container to player inventory
                if (!this.insertItem(itemStack2, 54, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory to container
                if (!this.insertItem(itemStack2, 0, 54, false)) {
                    return ItemStack.EMPTY;
                }
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
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean success = false;
        int currentIndex = startIndex;
        
        if (fromLast) {
            currentIndex = endIndex - 1;
        }

        // First try to stack with existing items
        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (fromLast) {
                    if (currentIndex < startIndex) {
                        break;
                    }
                } else {
                    if (currentIndex >= endIndex) {
                        break;
                    }
                }

                Slot slot = this.slots.get(currentIndex);
                ItemStack existingStack = slot.getStack();
                
                if (!existingStack.isEmpty() && ItemStack.canCombine(stack, existingStack)) {
                    int maxCount = slot.getMaxItemCount(stack);
                    int combinedCount = existingStack.getCount() + stack.getCount();
                    
                    if (combinedCount <= maxCount) {
                        stack.setCount(0);
                        existingStack.setCount(combinedCount);
                        slot.markDirty();
                        success = true;
                    } else if (existingStack.getCount() < maxCount) {
                        stack.decrement(maxCount - existingStack.getCount());
                        existingStack.setCount(maxCount);
                        slot.markDirty();
                        success = true;
                    }
                }

                if (fromLast) {
                    --currentIndex;
                } else {
                    ++currentIndex;
                }
            }
        }

        // Then try to put in empty slots
        if (!stack.isEmpty()) {
            if (fromLast) {
                currentIndex = endIndex - 1;
            } else {
                currentIndex = startIndex;
            }

            while (true) {
                if (fromLast) {
                    if (currentIndex < startIndex) {
                        break;
                    }
                } else {
                    if (currentIndex >= endIndex) {
                        break;
                    }
                }

                Slot slot = this.slots.get(currentIndex);
                ItemStack existingStack = slot.getStack();
                
                if (existingStack.isEmpty() && slot.canInsert(stack)) {
                    int maxCount = slot.getMaxItemCount(stack);
                    int insertCount = Math.min(stack.getCount(), maxCount);
                    slot.setStack(stack.split(insertCount));
                    slot.markDirty();
                    success = true;
                    
                    if (stack.isEmpty()) {
                        break;
                    }
                }

                if (fromLast) {
                    --currentIndex;
                } else {
                    ++currentIndex;
                }
            }
        }

        return success;
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
            blockEntity.setOpen(false);
            blockEntity.markDirty();
            blockEntity.updateAnimationState();
            
            // Play shulker box close sound
            if (!player.getWorld().isClient) {
                player.getWorld().playSound(null, blockEntity.getPos(), 
                        net.minecraft.sound.SoundEvents.BLOCK_SHULKER_BOX_CLOSE, 
                        net.minecraft.sound.SoundCategory.BLOCKS, 0.5F, 
                        player.getWorld().random.nextFloat() * 0.1F + 0.9F);
            }
        }
    }
}
