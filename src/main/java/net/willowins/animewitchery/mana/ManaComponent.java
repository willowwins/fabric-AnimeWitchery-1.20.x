package net.willowins.animewitchery.mana;

import net.minecraft.nbt.NbtCompound;

public class ManaComponent implements IManaComponent {
    private int mana = 10000;
    private final int maxMana = 10000;

    @Override
    public int getMana() { return mana; }
    @Override
    public int getMaxMana() { return maxMana; }

    @Override
    public void setMana(int value) {
        mana = Math.max(0, Math.min(value, maxMana));
    }

    @Override
    public boolean consume(int amount) {
        if (mana >= amount) { mana -= amount; return true; }
        return false;
    }

    @Override
    public void regen(int amount) { setMana(mana + amount); }

    @Override
    public void readFromNbt(NbtCompound tag) { mana = tag.getInt("Mana"); }
    @Override
    public void writeToNbt(NbtCompound tag) { tag.putInt("Mana", mana); }
}
