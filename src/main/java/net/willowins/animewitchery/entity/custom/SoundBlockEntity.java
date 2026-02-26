package net.willowins.animewitchery.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.block.entity.ModBlockEntities;

public class SoundBlockEntity extends BlockEntity {
    private long lastPlayedTime = -4000; // Start ready to play (more than cooldown ago)
    private static final int COOLDOWN_TICKS = 3900; // 3 minutes 15 seconds (duration of otherside)

    public SoundBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUND_BLOCK_ENTITY, pos, state);
    }

    public boolean canPlay(long currentTime) {
        return (currentTime - lastPlayedTime) >= COOLDOWN_TICKS;
    }

    public void markPlayed(long currentTime) {
        this.lastPlayedTime = currentTime;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("LastPlayedTime", lastPlayedTime);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        lastPlayedTime = nbt.getLong("LastPlayedTime");
    }
}
