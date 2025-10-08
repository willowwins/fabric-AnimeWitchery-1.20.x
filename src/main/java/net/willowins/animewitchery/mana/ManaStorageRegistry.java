package net.willowins.animewitchery.mana;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;
import net.willowins.animewitchery.item.custom.ResonantlCatalystItem;

import java.util.ArrayList;
import java.util.List;

public class ManaStorageRegistry {
    // === List of items that contribute to player mana ===
    private static final List<Item> MANA_STORAGE_ITEMS = new ArrayList<>();

    static {
        register(AlchemicalCatalystItem.INSTANCE);
        register(ResonantlCatalystItem.INSTANCE);
    }

    public static void register(Item item) {
        if (!MANA_STORAGE_ITEMS.contains(item)) {
            MANA_STORAGE_ITEMS.add(item);
        }
    }

    public static boolean isRegistered(Item item) {
        return MANA_STORAGE_ITEMS.contains(item);
    }

    public static List<Item> getRegisteredItems() {
        return MANA_STORAGE_ITEMS;
    }

    // === Mana aggregation helpers ===

    public static int getStoredManaFromItems(PlayerEntity player) {
        int total = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (isRegistered(stack.getItem())) {
                if (stack.getItem() instanceof ResonantlCatalystItem) {
                    total += ResonantlCatalystItem.getStoredMana(stack);
                }
                // Add other item type checks here as needed
            }
        }
        return total;
    }

    public static void consumeFromStorage(PlayerEntity player, int amount) {
        for (ItemStack stack : player.getInventory().main) {
            if (amount <= 0) return;
            if (isRegistered(stack.getItem())) {
                if (stack.getItem() instanceof ResonantlCatalystItem) {
                    int stored = ResonantlCatalystItem.getStoredMana(stack);
                    int drain = Math.min(stored, amount);
                    ResonantlCatalystItem.setStoredMana(stack, stored - drain);
                    amount -= drain;
                }
            }
        }
    }

    public static int getTotalStorageCapacity(PlayerEntity player) {
        int total = 0;

        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();

            // Skip anything not registered in the mana storage list
            if (!isRegistered(item)) continue;

            // If the item defines a MAX_MANA field, read it via reflection
            try {
                var field = item.getClass().getDeclaredField("MAX_MANA");
                field.setAccessible(true);
                int capacity = field.getInt(null); // static field → null instance
                total += capacity;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
                // If the item doesn't define MAX_MANA, just skip it silently
            }
        }

        return total;
    }

}
