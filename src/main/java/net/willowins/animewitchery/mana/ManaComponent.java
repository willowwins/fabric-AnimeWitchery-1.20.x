package net.willowins.animewitchery.mana;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;

public class ManaComponent implements IManaComponent, AutoSyncedComponent {
    private int mana;
    private final int maxMana = 10000;
    private final PlayerEntity player;

    public ManaComponent(PlayerEntity player) {
        this.player = player;
        this.mana = maxMana;
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        return maxMana;
    }

    @Override
    public void setMana(int amount) {
        mana = Math.max(0, Math.min(amount, maxMana));
        // Sync the change to client
        ModComponents.PLAYER_MANA.sync(player);
    }

    @Override
    public boolean consume(int amount) {
        if (mana >= amount) {
            mana -= amount;
            ModComponents.PLAYER_MANA.sync(player);
            return true;
        }
        return false;
    }

    @Override
    public void regen(int amount) {
        setMana(mana + amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        mana = tag.getInt("player_mana");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("player_mana", mana);
    }
}
