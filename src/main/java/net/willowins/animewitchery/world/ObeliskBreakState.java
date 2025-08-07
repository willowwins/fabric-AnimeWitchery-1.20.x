package net.willowins.animewitchery.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

public class ObeliskBreakState extends PersistentState {
    private boolean obeliskBreakOncePlayed = false;
    
    public ObeliskBreakState() {
        super();
    }
    
    public static ObeliskBreakState get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            ObeliskBreakState::fromNbt,
            ObeliskBreakState::new,
            "animewitchery_obelisk_break"
        );
    }
    
    public boolean hasPlayedOnce() {
        return obeliskBreakOncePlayed;
    }
    
    public void markAsPlayed() {
        this.obeliskBreakOncePlayed = true;
        this.markDirty();
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("obelisk_break_once_played", obeliskBreakOncePlayed);
        return nbt;
    }
    
    public static ObeliskBreakState fromNbt(NbtCompound nbt) {
        ObeliskBreakState state = new ObeliskBreakState();
        state.obeliskBreakOncePlayed = nbt.getBoolean("obelisk_break_once_played");
        return state;
    }
} 