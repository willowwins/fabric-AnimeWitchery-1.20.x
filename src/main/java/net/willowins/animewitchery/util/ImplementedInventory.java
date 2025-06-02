package net.willowins.animewitchery.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface ImplementedInventory extends Inventory {
    DefaultedList<ItemStack> getInventory();

    @Override
    default int size() {
        return getInventory().size();
    }

    static ImplementedInventory of(DefaultedList<ItemStack> items) {
        return new ImplementedInventory() {
            @Override
            public DefaultedList<ItemStack> getInventory() {
                return items; // ✅ CORRECT: return the actual list passed in
            }

            @Override
            public void markDirty() {
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return true; // or implement proper access check
            }
        };
    }


    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getInventory()) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        if (slot < 0 || slot >= size()) {
            return ItemStack.EMPTY;
        }
        return getInventory().get(slot);
    }
    @Override
    default ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(getInventory(), slot, amount);
    }

    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getInventory(), slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getInventory().set(slot, stack);
    }

    @Override
    default void clear() {
        getInventory().clear();
    }
   }