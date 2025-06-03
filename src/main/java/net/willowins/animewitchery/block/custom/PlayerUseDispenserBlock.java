package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.PlayerUseDispenserBlockEntity;

public class PlayerUseDispenserBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.FACING;

    public PlayerUseDispenserBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ExtendedScreenHandlerFactory factory) {
                player.openHandledScreen(factory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            boolean powered = world.isReceivingRedstonePower(pos);
            if (powered) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof PlayerUseDispenserBlockEntity dispenser) {
                    dispenser.dispenseBlock();
                }
            }
        }
    }


    private void useItemFromDispenser(ServerWorld world, BlockPos pos, Direction facing) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof PlayerUseDispenserBlockEntity dispenser)) return;

        ItemStack stack = dispenser.getStack(0);
        if (stack.isEmpty()) return;

        PlayerEntity closest = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, false);
        if (closest == null) return;

        ItemStack heldCopy = stack.copy();
        TypedActionResult<ItemStack> result = heldCopy.use(world, closest, Hand.MAIN_HAND);

        if (result.getResult().isAccepted()) {
            dispenser.setStack(0, result.getValue());
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PlayerUseDispenserBlockEntity(pos, state);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SidedInventory inventory) {
            return net.minecraft.screen.ScreenHandler.calculateComparatorOutput(inventory);
        }
        return 0;
    }
}
