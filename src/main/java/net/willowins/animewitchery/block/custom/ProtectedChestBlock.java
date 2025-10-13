package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.block.enums.ChestType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.ProtectedChestBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ProtectedChestBlock extends TrappedChestBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public ProtectedChestBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CHEST_TYPE, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(CHEST_TYPE, ChestType.SINGLE).with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid() == net.minecraft.fluid.Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        
        if (!world.isClient && placer instanceof PlayerEntity player) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProtectedChestBlockEntity chest) {
                chest.setOwner(player.getUuid(), player.getName().getString());
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ProtectedChestBlockEntity chest)) {
            return ActionResult.PASS;
        }

        ItemStack heldItem = player.getStackInHand(hand);

        // Handle permission management with named paper
        if (heldItem.getItem() == net.minecraft.item.Items.PAPER && heldItem.hasCustomName()) {
            String playerName = heldItem.getName().getString();
            
            if (!chest.isOwner(player)) {
                player.sendMessage(Text.literal("§cOnly the owner can manage permissions!"), true);
                return ActionResult.FAIL;
            }

            if (player.isSneaking()) {
                // Remove permission
                if (chest.removeAuthorizedPlayer(playerName)) {
                    player.sendMessage(Text.literal("§eRemoved " + playerName + " from access list"), false);
                } else {
                    player.sendMessage(Text.literal("§c" + playerName + " was not in the access list"), false);
                }
            } else {
                // Add permission
                if (chest.addAuthorizedPlayer(playerName)) {
                    player.sendMessage(Text.literal("§aAdded " + playerName + " to access list"), false);
                } else {
                    player.sendMessage(Text.literal("§e" + playerName + " already has access"), false);
                }
            }
            return ActionResult.SUCCESS;
        }

        // Check if player has permission to access
        if (!chest.isOwner(player) && !chest.isAuthorized(player.getName().getString())) {
            player.sendMessage(Text.literal("§cThis chest is protected! Only " + chest.getOwnerName() + " and authorized players can access it."), true);
            return ActionResult.FAIL;
        }

        // Open the chest inventory
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
        }

        return ActionResult.CONSUME;
    }


    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>)ticker : null;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProtectedChestBlockEntity chest) {
                if (!chest.isOwner(player) && !player.isCreative()) {
                    player.sendMessage(Text.literal("§cOnly the owner can break this chest!"), true);
                    // Cancel the break by not calling super
                    return;
                }
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ProtectedChestBlockEntity chest) {
                ItemScatterer.spawn(world, pos, chest);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ProtectedChestBlockEntity(pos, state);
    }


    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return net.minecraft.screen.ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}

