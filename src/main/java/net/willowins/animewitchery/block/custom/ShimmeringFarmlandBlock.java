package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ShimmeringFarmlandBlock extends FarmlandBlock {

    public ShimmeringFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        // Only act if the farmland has moisture
        if (state.get(MOISTURE) <= 0) return;

        BlockPos abovePos = pos.up();
        BlockState aboveState = world.getBlockState(abovePos);
        Block block = aboveState.getBlock();

        // ✦ Growth boost: safe, does not call randomTick
        boolean grew = false;

        // 1. Vanilla crops (wheat, carrots, potatoes, beetroot)
        if (block instanceof CropBlock crop) {
            if (!crop.isMature(aboveState)) {
                crop.applyGrowth(world, abovePos, aboveState);
                grew = true;
            }
        }
        // 2. Any block that implements Fertilizable (modded crops, stems, etc.)
        else if (block instanceof Fertilizable fertilizable) {
            if (fertilizable.isFertilizable(world, abovePos, aboveState, world.isClient)
                    && fertilizable.canGrow(world, random, abovePos, aboveState)) {
                fertilizable.grow(world, random, abovePos, aboveState);
                grew = true;
            }
        }

        // Optional: could spawn extra particles if it actually grew
        if (grew) {
            world.spawnParticles(
                    ParticleTypes.END_ROD,
                    abovePos.getX() + 0.5,
                    abovePos.getY() + 0.5,
                    abovePos.getZ() + 0.5,
                    2, 0.15, 0.15, 0.15, 0.0
            );
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(MOISTURE) > 0 && random.nextFloat() < 0.3f) { // Client tick rate is high, 30% is fairly constant
            world.addParticle(
                    ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    pos.getY() + 1.1 + (random.nextDouble() * 0.2),
                    pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.01, 0.0
            );
        }
    }
}