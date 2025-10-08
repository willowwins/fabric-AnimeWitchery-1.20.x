package net.willowins.animewitchery.mana;

import net.minecraft.entity.player.PlayerEntity;

public class ManaUtils {

    public static int getTotalMana(PlayerEntity player) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        return mana.getMana() + ManaStorageRegistry.getStoredManaFromItems(player);
    }

    public static int getTotalMaxMana(PlayerEntity player) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        return mana.getMaxMana() + ManaStorageRegistry.getTotalStorageCapacity(player);
    }

    public static boolean consumeWithStorage(PlayerEntity player, int amount) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);

        int playerMana = mana.getMana();
        int total = playerMana + ManaStorageRegistry.getStoredManaFromItems(player);
        if (total < amount) return false;

        int remaining = amount;

        if (playerMana > 0) {
            int used = Math.min(playerMana, remaining);
            mana.consume(used);
            remaining -= used;
        }

        if (remaining > 0) {
            ManaStorageRegistry.consumeFromStorage(player, remaining);
        }

        return true;
    }
}
