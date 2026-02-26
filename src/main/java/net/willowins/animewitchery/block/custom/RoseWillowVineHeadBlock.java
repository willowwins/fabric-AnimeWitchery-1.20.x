package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;

public class RoseWillowVineHeadBlock extends CaveVinesHeadBlock {
    public RoseWillowVineHeadBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected Block getPlant() {
        return ModBlocks.ROSEWILLOW_VINES;
    }

    @Override
    protected int getGrowthLength(Random random) {
        return 1;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.ROSEWILLOW_VINES);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        return RoseWillowVineBodyBlock.pickBerries(player, state, world, pos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        // Allow placement on leaves in addition to default blocks
        return block instanceof LeavesBlock || super.canPlaceAt(state, world, pos);
    }

    @Override
    public void randomTick(BlockState state, net.minecraft.server.world.ServerWorld world, BlockPos pos,
            net.minecraft.util.math.random.Random random) {
        // 15% chance to grow a large rosewillow blossom instead of extending the vine
        if (random.nextFloat() < 0.15f) {
            BlockPos belowPos = pos.down();
            if (world.isAir(belowPos) && canGrowAt(world, belowPos)) {
                // Place large rosewillow blossom below
                world.setBlockState(belowPos, ModBlocks.LARGE_ROSEWILLOW_BLOSSOM.getDefaultState(), 3);
                return;
            }
        }

        // Otherwise, use default vine growth behavior
        super.randomTick(state, world, pos, random);
    }

    private boolean canGrowAt(net.minecraft.world.WorldView world, BlockPos pos) {
        BlockPos above = pos.up();
        BlockState aboveState = world.getBlockState(above);
        // Can grow if there's a vine or leaves above
        return aboveState.isOf(ModBlocks.ROSEWILLOW_VINES)
                || aboveState.isOf(ModBlocks.ROSEWILLOW_VINES_TIP)
                || aboveState.getBlock() instanceof LeavesBlock;
    }
}
