package net.willowins.animewitchery.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;

public class RosewillowBlossomItem extends Item {
    public RosewillowBlossomItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction side = context.getSide();

        // Check if clicking on the bottom of a block (to place vines hanging down)
        if (side == Direction.DOWN && !world.isClient) {
            BlockPos vinePos = pos.down();

            // Check if there's air below to place vines
            if (world.isAir(vinePos)) {
                placeVines(world, vinePos, context.getPlayer());

                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getStack().decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    private void placeVines(World world, BlockPos startPos, PlayerEntity player) {
        Random random = world.getRandom();
        int vineLength = 1 + random.nextInt(4); // 1-4 blocks of vines

        BlockPos currentPos = startPos;
        for (int i = 0; i < vineLength; i++) {
            if (world.isAir(currentPos)) {
                if (i == vineLength - 1) {
                    // Last segment is the tip
                    world.setBlockState(currentPos,
                            ModBlocks.ROSEWILLOW_VINES_TIP.getDefaultState()
                                    .with(Properties.BERRIES, random.nextFloat() < 0.3f),
                            3);
                } else {
                    // Middle segments are body
                    world.setBlockState(currentPos,
                            ModBlocks.ROSEWILLOW_VINES.getDefaultState()
                                    .with(Properties.BERRIES, random.nextFloat() < 0.3f),
                            3);
                }
                currentPos = currentPos.down();
            } else {
                break;
            }
        }
    }
}
