package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.GrowthAcceleratorBlockEntity;
import net.willowins.animewitchery.block.entity.ItemActionBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.screen.GrowthAcceleratorScreenHandler;


public class GrowthAcceleratorBlock extends BlockWithEntity implements BlockEntityProvider {

    public GrowthAcceleratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntities.GROWTH_ACCELERATOR_BLOCK_ENTITY, GrowthAcceleratorBlockEntity::tick);
    }



    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowthAcceleratorBlockEntity(pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrowthAcceleratorBlockEntity) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity playerEntity) {
                        return new GrowthAcceleratorScreenHandler(syncId, inv, (GrowthAcceleratorBlockEntity) blockEntity);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.literal("Growth Accelerator");
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
                        buf.writeBlockPos(pos);  // send block position to client
                    }
                });
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof GrowthAcceleratorBlockEntity entity) {
                entity.dropInventory(world, pos);
                world.removeBlockEntity(pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // No need here - logic handled by BlockEntity tick
    }

}

