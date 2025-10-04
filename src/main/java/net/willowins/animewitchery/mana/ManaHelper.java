package net.willowins.animewitchery.mana;  // or mana, or common

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

public class ManaHelper {
    /**
     * Attempts to consume {@code cost} mana from the player’s mana component + catalysts in inventory.
     * Returns true if fully paid; false otherwise (partial changes may already have applied — consider rolling back if needed).
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

        // Drain catalysts
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof AlchemicalCatalystItem) {
                int stored = AlchemicalCatalystItem.getStoredMana(stack);
                if (stored <= 0) continue;
                int take = Math.min(stored, remaining);
                AlchemicalCatalystItem.setStoredMana(stack, stored - take);
                remaining -= take;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return (remaining <= 0);
    }
}
