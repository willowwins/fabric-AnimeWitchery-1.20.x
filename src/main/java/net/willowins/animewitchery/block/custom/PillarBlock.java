package net.willowins.animewitchery.block.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Direction;

public class PillarBlock extends net.minecraft.block.PillarBlock {
    public PillarBlock(AbstractBlock.Settings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.15, 0, 0.15, 0.85, 1, 0.85);
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false; // Disable face culling
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true; // Make it transparent to prevent culling
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Use a smaller collision shape to prevent interference
        return VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 1, 0.75);
    }

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true; // Enable sided transparency
    }
}
