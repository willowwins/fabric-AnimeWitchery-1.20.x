package net.willowins.animewitchery.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerMana {
    private int mana;
    private final int maxMana;

    public PlayerMana(int maxMana) {
        this.maxMana = maxMana;
        this.mana = maxMana; // spawn full
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void addMana(int amount) {
        mana = Math.min(maxMana, mana + amount);
    }

    public boolean consumeMana(int amount) {
        if (mana >= amount) {
            mana -= amount;
            return true;
        }
        return false;
    }

    public void readNbt(NbtCompound nbt) {
        mana = nbt.getInt("Mana");
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Mana", mana);
    }
}
