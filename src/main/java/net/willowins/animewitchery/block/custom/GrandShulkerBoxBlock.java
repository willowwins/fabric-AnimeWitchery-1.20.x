package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GrandShulkerBoxBlock extends ShulkerBoxBlock {
    public static final DirectionProperty FACING = Properties.FACING;
    private final DyeColor color;

    public GrandShulkerBoxBlock(DyeColor color, Settings settings) {
        super(color, settings);
        this.color = color;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrandShulkerBoxBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            
            // Check if player is holding a dye - handle this FIRST to prevent vanilla behavior
            if (itemStack.getItem() instanceof DyeItem dyeItem) {
                DyeColor dyeColor = dyeItem.getColor();
                if (dyeColor != this.color) {
                    // Change the color of the Grand Shulker Box by replacing the block
                    BlockState newState = getColoredBlock(dyeColor).getDefaultState().with(FACING, state.get(FACING));
                    world.setBlockState(pos, newState);
                    
                    // Transfer block entity data
                    BlockEntity oldEntity = world.getBlockEntity(pos);
                    if (oldEntity instanceof GrandShulkerBoxBlockEntity oldGrandBox) {
                        // Store the old data
                        NbtCompound oldNbt = oldGrandBox.createNbt();
                        
                        // Remove the old block entity
                        world.removeBlockEntity(pos);
                        
                        // The new block will create its own block entity automatically
                        // We'll transfer the data after the block is placed
                        BlockEntity newEntity = world.getBlockEntity(pos);
                        if (newEntity instanceof GrandShulkerBoxBlockEntity newGrandBox) {
                            newGrandBox.readNbt(oldNbt);
                        }
                    }
                    
                    // Force block update to ensure client synchronization
                    world.updateListeners(pos, state, newState, 3);
                    
                    // Consume the dye if not in creative mode
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    
                    return ActionResult.SUCCESS;
                }
                // If it's the same color, don't do anything
                return ActionResult.SUCCESS;
            }
            
            // Open the inventory if not dyeing
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrandShulkerBoxBlockEntity entity) {
                player.openHandledScreen(entity);
                return ActionResult.SUCCESS;
            }
        }
        
        // Don't call super.onUse() to prevent vanilla shulker box behavior
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrandShulkerBoxBlockEntity entity) {
                if (!world.isClient && !world.getBlockState(pos).isOf(this)) {
                    // Create an item stack
                    ItemStack itemStack = new ItemStack(this);

                    // Only add NBT data if the inventory is not empty
                    if (!entity.isEmpty()) {
                        NbtCompound blockEntityTag = entity.createNbtWithIdentifyingData();
                        NbtCompound itemNbt = new NbtCompound();
                        itemNbt.put("BlockEntityTag", blockEntityTag);
                        itemStack.setNbt(itemNbt);
                    }

                    // Drop the item with preserved NBT (if any)
                    ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
                    world.updateComparators(pos, this);
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    // Helper method to get the colored block for a given dye color
    private static Block getColoredBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_WHITE;
            case ORANGE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_ORANGE;
            case MAGENTA -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_MAGENTA;
            case LIGHT_BLUE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIGHT_BLUE;
            case YELLOW -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_YELLOW;
            case LIME -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIME;
            case PINK -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_PINK;
            case GRAY -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_GRAY;
            case LIGHT_GRAY -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_LIGHT_GRAY;
            case CYAN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_CYAN;
            case PURPLE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_PURPLE;
            case BLUE -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BLUE;
            case BROWN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BROWN;
            case GREEN -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_GREEN;
            case RED -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_RED;
            case BLACK -> net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_BLACK;
        };
    }
}