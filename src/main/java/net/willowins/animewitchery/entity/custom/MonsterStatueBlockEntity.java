package net.willowins.animewitchery.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.custom.MonsterStatueBlock;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.sound.ModSounds;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MonsterStatueBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int soundTimer = 0;

    // Song is 1 minute 18 seconds (78 seconds = 1560 ticks)
    private static final int LOOP_INTERVAL = 1560;

    public MonsterStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MONSTER_STATUE_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MonsterStatueBlockEntity entity) {
        // Play sound on server side so it broadcasts properly to all clients as
        // positional
        if (!world.isClient) {
            if (state.get(MonsterStatueBlock.ACTIVATED) && world.isRaining()) {
                if (entity.soundTimer <= 0) {
                    // Play positional sound from server (null = broadcast to all nearby players)
                    world.playSound(
                            null, // null player = broadcast to all
                            pos,
                            ModSounds.MONSTER_STATUE_AMBIENT,
                            SoundCategory.BLOCKS,
                            1.0f, // volume
                            1.0f // pitch
                    );
                    AnimeWitchery.LOGGER.info("Playing Monster Statue sound at " + pos.toShortString());
                    entity.soundTimer = LOOP_INTERVAL;
                } else {
                    entity.soundTimer--;
                }
            } else {
                entity.soundTimer = 0; // Reset if conditions fail, so it restarts immediately when valid
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
