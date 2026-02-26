package net.willowins.animewitchery.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.SporeBlossomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.willowins.animewitchery.block.ModBlocks;

public class LargeRosewillowBlossomBlock extends SporeBlossomBlock {
    public LargeRosewillowBlossomBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        return super.canPlaceAt(state, world, pos) || blockState.isOf(ModBlocks.ROSEWILLOW_VINES)
                || blockState.isOf(ModBlocks.ROSEWILLOW_VINES_TIP) || blockState.isOf(ModBlocks.ROSEWILLOW_LEAVES);
    }
}
