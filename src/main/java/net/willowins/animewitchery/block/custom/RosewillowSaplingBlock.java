package net.willowins.animewitchery.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.willowins.animewitchery.block.ModBlocks;

public class RosewillowSaplingBlock extends SaplingBlock {
    public RosewillowSaplingBlock(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
    }

    @Override
    public boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(ModBlocks.ROSEWILLOW_ROOTS) || super.canPlantOnTop(floor, world, pos);
    }
}
