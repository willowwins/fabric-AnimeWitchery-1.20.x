package net.willowins.animewitchery.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class ShimmeringFarmlandBlock extends FarmlandBlock {

    public ShimmeringFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);

        if (state.get(MOISTURE) > 0) {
            BlockPos abovePos = pos.up();
            BlockState aboveState = world.getBlockState(abovePos);

            if (aboveState.getBlock() instanceof PlantBlock || aboveState.getBlock() instanceof CropBlock) {
                // Apply an extra tick to the plant above to double growth speed
                aboveState.randomTick(world, abovePos, random);
            }
        }
    }
}
