package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
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
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class BarrierCircleBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public BarrierCircleBlock(Settings settings) {
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
            if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
                ItemStack heldItem = player.getStackInHand(hand);
                
                // Stage 2: Barrier Catalyst defines circle type
                if (heldItem.isOf(ModItems.BARRIER_CATALYST)) {
                    if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.BASIC) {
                        System.out.println("BarrierCircle: Advancing from BASIC to DEFINED");
                        circleEntity.setCircleType(BarrierCircleBlockEntity.CircleType.BARRIER);
                        circleEntity.setStage(BarrierCircleBlockEntity.CircleStage.DEFINED);
                        return ActionResult.SUCCESS;
                    }
                }
                
                // Stage 3: Magic Chalk completes the circle (from DEFINED to COMPLETE)
                if (heldItem.isOf(ModItems.MAGIC_CHALK)) {
                    if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.DEFINED) {
                        System.out.println("BarrierCircle: Advancing from DEFINED to COMPLETE");
                        circleEntity.setStage(BarrierCircleBlockEntity.CircleStage.COMPLETE);
                        return ActionResult.SUCCESS;
                    } else if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.BASIC) {
                        System.out.println("BarrierCircle: Already in BASIC stage, use Barrier Catalyst next");
                        return ActionResult.SUCCESS;
                    } else if (circleEntity.getStage() == BarrierCircleBlockEntity.CircleStage.COMPLETE) {
                        System.out.println("BarrierCircle: Already complete!");
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.BARRIER_CIRCLE_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return null; // No ticker needed for this block entity
    }
} 