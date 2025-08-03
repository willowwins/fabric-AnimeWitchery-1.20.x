package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ActiveBindingSpellBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Track lifetime
    private int lifeTicks = 0;
   private static final int MAX_LIFE_TICKS = 20 * 30; // 30 seconds (20 ticks/sec)

    public ActiveBindingSpellBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Original idle animation remains
        controllerRegistrar.add(new AnimationController<>(this, "Idle", 0, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /**
     * Called every tick by the block's ticker (server-side only).
     */
    public static void tick(World world, BlockPos pos, BlockState state, ActiveBindingSpellBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return; // Only run on server

        blockEntity.lifeTicks++;

        if (blockEntity.lifeTicks >= MAX_LIFE_TICKS) {
            serverWorld.breakBlock(pos, false); // false = no drops
        }
    }

}
