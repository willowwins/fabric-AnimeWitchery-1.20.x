package net.willowins.animewitchery.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.sound.ModSounds;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ActiveObeliskBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private SoundInstance humSound;

    public ActiveObeliskBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "Active", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("active"))));
    }

    public void startHumSound() {
        // This will be called from the renderer on client side
        if (world != null && world.isClient) {
            world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                ModSounds.OBELISK_HUM, SoundCategory.AMBIENT, 0.3f, 1.0f, false);
        }
    }

    public void stopHumSound() {
        // This will be called when the obelisk is deactivated
        if (world != null && world.isClient) {
            // Stop any ongoing hum sounds by playing a silent sound
            // The hum sound will naturally stop when the block entity is removed
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ActiveObeliskBlockEntity entity) {
        // Play hum sound every 2 seconds (40 ticks)
        if (world.getTime() % 40 == 0 && world.isClient) {
            entity.startHumSound();
        }
    }
} 