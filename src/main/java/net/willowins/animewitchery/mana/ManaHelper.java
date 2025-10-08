package net.willowins.animewitchery.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.mana.ManaStorageRegistry;

public class ManaHelper {

    /**
     * Returns the total effective mana available to the player:
     * player’s internal mana + all stored mana from registered items.
     */
    public static int getTotalMana(PlayerEntity player) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int total = mana.getMana();

        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();
            if (!ManaStorageRegistry.isRegistered(item)) continue;

            try {
                var getStored = item.getClass().getMethod("getStoredMana", ItemStack.class);
                int stored = (int) getStored.invoke(item, stack);
                total += stored;
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return total;
    }

    /**
     * Returns the maximum mana capacity: player + all registered items.
     */
    public static int getTotalMaxMana(PlayerEntity player) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int total = mana.getMaxMana();

        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();
            if (!ManaStorageRegistry.isRegistered(item)) continue;

            try {
                var maxField = item.getClass().getDeclaredField("MAX_MANA");
                maxField.setAccessible(true);
                total += maxField.getInt(item);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return total;
    }

    /**
     * Attempts to consume {@code cost} mana from the player and all registered items.
     */
    public static boolean consumeCostFromPlayerAndCatalysts(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int current = mana.getMana();

        if (current >= cost) {
            mana.consume(cost);
            return true;
        }

        int remaining = cost - current;
        if (current > 0) {
            mana.consume(current);
        }

        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();
            if (!ManaStorageRegistry.isRegistered(item)) continue;

            try {
                var getStored = item.getClass().getMethod("getStoredMana", ItemStack.class);
                var setStored = item.getClass().getMethod("setStoredMana", ItemStack.class, int.class);

                int stored = (int) getStored.invoke(item, stack);
                if (stored <= 0) continue;

                int take = Math.min(stored, remaining);
                setStored.invoke(item, stack, stored - take);
                remaining -= take;

                if (remaining <= 0) break;
            } catch (ReflectiveOperationException ignored) {
            }
        }

        return remaining <= 0;
    }

    /**
     * Returns true if the player currently has any registered mana-storage item.
     */
    public static boolean hasManaStorageItem(PlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (ManaStorageRegistry.isRegistered(stack.getItem())) return true;
        }
        return false;
    }
}
