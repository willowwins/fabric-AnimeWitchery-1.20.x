package net.willowins.animewitchery.mana;

import dev.emi.trinkets.api.TrinketsApi;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.item.custom.LatentManaPendantItem;

public class ManaComponent implements IManaComponent, AutoSyncedComponent {
    private int mana;
    private final int baseMaxMana = 10000;
    private final PlayerEntity player;

    public ManaComponent(PlayerEntity player) {
        this.player = player;
        this.mana = baseMaxMana;
    }

    @Override
    public int getMana() {
        return mana;
    }

    @Override
    public int getMaxMana() {
        int[] totalMax = {baseMaxMana}; // Use array to allow modification in lambda
        
        // Check for Latent Mana Pendant in trinket slots
        if (TrinketsApi.getTrinketComponent(player).isPresent()) {
            var trinketComponent = TrinketsApi.getTrinketComponent(player).get();
            trinketComponent.getAllEquipped().forEach(pair -> {
                if (pair.getRight().getItem() instanceof LatentManaPendantItem) {
                    // Add bonus mana per pendant equipped
                    totalMax[0] += LatentManaPendantItem.getBonusMana(player);
                }
            });
        }
        
        return totalMax[0];
    }

    @Override
    public void setMana(int amount) {
        mana = Math.max(0, Math.min(amount, getMaxMana()));
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
