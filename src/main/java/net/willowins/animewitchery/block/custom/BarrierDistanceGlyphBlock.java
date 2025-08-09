package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.BarrierDistanceGlyphBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class BarrierDistanceGlyphBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public BarrierDistanceGlyphBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BarrierDistanceGlyphBlockEntity glyphEntity) {
                ItemStack heldItem = player.getStackInHand(hand);
                
                // Normal Chalk can convert this back to a barrier circle
                if (heldItem.isOf(ModItems.CHALK)) {
                    System.out.println("BarrierDistanceGlyph: Converting to BARRIER_CIRCLE");
                    // Convert this block back to a barrier circle
                    world.setBlockState(pos, ModBlocks.BARRIER_CIRCLE.getDefaultState());
                    return ActionResult.SUCCESS;
                }
                
                // Magic Chalk can be used to define the distance/size
                if (heldItem.isOf(ModItems.MAGIC_CHALK)) {
                    System.out.println("BarrierDistanceGlyph: Defining distance/size parameters");
                    // TODO: Add distance/size definition logic here
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.BARRIER_DISTANCE_GLYPH_BLOCK_ENTITY.instantiate(pos, state);
    }
}
