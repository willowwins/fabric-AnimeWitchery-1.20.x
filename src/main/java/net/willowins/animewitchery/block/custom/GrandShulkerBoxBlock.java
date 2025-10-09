package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.ItemPlacementContext;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.GrandShulkerBoxBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GrandShulkerBoxBlock extends ShulkerBoxBlock {
    private final DyeColor color;

    public GrandShulkerBoxBlock(DyeColor color, Settings settings) {
        super(color, settings);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrandShulkerBoxBlockEntity(pos, state);
    }


    public BlockEntityType<?> getBlockEntityType() {
        return net.willowins.animewitchery.block.ModBlocks.GRAND_SHULKER_BOX_ENTITY;
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Always place the block in the same orientation regardless of placement surface
        return this.getDefaultState();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            // Open the inventory
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrandShulkerBoxBlockEntity entity) {
                player.openHandledScreen(entity);
                return ActionResult.SUCCESS;
            }
        }
        
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrandShulkerBoxBlockEntity entity) {
                if (!world.isClient && !world.getBlockState(pos).isOf(this)) {
                    // Drop the correct colored Grand Shulker Box item based on the block that was broken
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

}