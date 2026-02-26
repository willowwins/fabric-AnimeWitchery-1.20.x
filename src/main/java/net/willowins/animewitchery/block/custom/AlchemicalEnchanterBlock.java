package net.willowins.animewitchery.block.custom;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.AlchemicalEnchanterBlockEntity;
import org.jetbrains.annotations.Nullable;

public class AlchemicalEnchanterBlock extends BlockWithEntity {

    public AlchemicalEnchanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalEnchanterBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AlchemicalEnchanterBlockEntity) {
                ItemScatterer.spawn(world, pos, (AlchemicalEnchanterBlockEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    /**
     * Calculates enchanting power based on the structure around the block.
     * 
     * /**
     * Calculates enchanting power based on the structure around the block.
     * Chiseled Stone Bricks: +2 power
     * Amethyst Clusters: +5 power
     */
    private int calculateEnchantingPower(World world, BlockPos pos) {
        int power = 0;

        // Check a 5x5 area (offset -2 to +2) on the same Y level as the enchanter
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0)
                    continue; // Skip center

                BlockPos checkPos = pos.add(x, 0, z);
                BlockState state = world.getBlockState(checkPos);

                if (state.isOf(Blocks.CHISELED_STONE_BRICKS)) {
                    power += 2;
                } else if (state.isOf(Blocks.AMETHYST_CLUSTER)) {
                    power += 5;
                }
            }
        }

        // Also check positions below chiseled bricks (to allow rings or pillars)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                BlockPos checkPos = pos.add(x, -1, z);
                if (world.getBlockState(checkPos).isOf(Blocks.CHISELED_STONE_BRICKS)) {
                    power += 1; // Bricks below give half power
                }
            }
        }

        return Math.min(power, 30); // Cap at level 30
    }
}
