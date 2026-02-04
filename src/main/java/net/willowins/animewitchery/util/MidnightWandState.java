package net.willowins.animewitchery.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

/**
 * Persistent state to track when midnight enforcement should end
 */
public class MidnightWandState extends PersistentState {
    private static final String KEY = "animewitchery_midnight_wand";
    private long midnightEndTime = 0;
    
    public MidnightWandState() {
    }
    
    public static MidnightWandState getOrCreate(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(
            MidnightWandState::fromNbt,
            MidnightWandState::new,
            KEY
        );
    }
    
    public static MidnightWandState fromNbt(NbtCompound nbt) {
        MidnightWandState state = new MidnightWandState();
        state.midnightEndTime = nbt.getLong("MidnightEndTime");
        return state;
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("MidnightEndTime", midnightEndTime);
        return nbt;
    }
    
    public void setMidnightEndTime(long endTime) {
        this.midnightEndTime = endTime;
        markDirty();
    }
    
    public long getMidnightEndTime() {
        return midnightEndTime;
    }
    
    public boolean isMidnightActive(long currentTime) {
        return midnightEndTime > 0 && currentTime < midnightEndTime;
    }
}


