package net.willowins.animewitchery.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StarlightFluidBlock extends FluidBlock {
    public StarlightFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 200, 0));
            } else if (entity instanceof net.minecraft.entity.ItemEntity itemEntity) {
                if (itemEntity.getStack().getItem() == net.minecraft.item.Items.BLAZE_POWDER) {
                    if (world.random.nextFloat() < 0.05f) { // ~5% chance per tick = ~1 second average
                        itemEntity.setStack(new net.minecraft.item.ItemStack(
                                net.willowins.animewitchery.item.ModItems.STARDUST, itemEntity.getStack().getCount()));
                        // Spawn particles on server-side (visible to clients)
                        ((net.minecraft.server.world.ServerWorld) world).spawnParticles(
                                net.minecraft.particle.ParticleTypes.END_ROD, itemEntity.getX(), itemEntity.getY(),
                                itemEntity.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
                        world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                                net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }
                }
            }
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
