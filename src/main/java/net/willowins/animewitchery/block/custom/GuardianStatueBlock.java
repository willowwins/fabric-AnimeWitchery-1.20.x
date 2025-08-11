package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class GuardianStatueBlock extends BlockWithEntity {
    
    // Define the collision shape for the statue based on the actual geo model
    // The model is 6.5 blocks tall and 4 blocks wide
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(2, 0, 2, 14, 16, 14),      // Base (Y=0-1)
        Block.createCuboidShape(1, 16, 1, 15, 32, 15)      // Lower body (Y=1-2)
        );

    public GuardianStatueBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.GUARDIAN_STATUE_BLOCK_ENTITY.instantiate(pos, state);
    }
}
