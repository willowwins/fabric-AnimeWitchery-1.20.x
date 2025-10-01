package net.willowins.animewitchery.block.custom;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.PlateBlockEntity;
import org.jetbrains.annotations.Nullable;


public class PlateBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    public static final BooleanProperty HAS_ITEM = BooleanProperty.of("has_item");

    public PlateBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(HAS_ITEM, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_ITEM);
    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 60);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        PlateBlockEntity be = (PlateBlockEntity) world.getBlockEntity(pos);
        if (be == null) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        ItemStack slot = be.getStack(0);

        if (slot.isEmpty() && !held.isEmpty()) {
            // place exactly one item on the plate
            ItemStack one = held.copy();
            one.setCount(1);
            be.setStack(0, one);   // marks dirty + syncs
            held.decrement(1);
            return ActionResult.CONSUME;
        }

        if (!slot.isEmpty()) {
            // return item to player (or drop if full)
            if (!player.getInventory().insertStack(slot.copy())) {
                player.dropItem(slot.copy(), false);
            }
            be.removeStack(0); // marks dirty + syncs
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }



    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.PLATE_BLOCK_ENTITY.instantiate(pos, state);
    }
}


