package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;
import net.willowins.animewitchery.ModScreenHandlers;
import net.willowins.animewitchery.block.entity.AutoCrafterBlockEntity;
import net.willowins.animewitchery.block.entity.ItemActionBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.screen.ItemActionScreenHandler;

public class ItemActionBlock extends BlockWithEntity {

    public ItemActionBlock(Settings settings) {
        super(settings);
    }
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ItemActionBlockEntity(pos, state);
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ItemActionBlockEntity itemActionBE) {
                player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity playerEntity) {
                        return new ItemActionScreenHandler(ModScreenHandlers.ITEM_ACTION_SCREEN_HANDLER, syncId, inv, itemActionBE);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.literal("Item Action");
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                        buf.writeBlockPos(pos);  // send block position to client for screen handler syncing
                    }
                });
            }
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ItemActionBlockEntity entity) {
                entity.dropInventory(world, pos);
                world.removeBlockEntity(pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ITEM_ACTION_BLOCK_ENTITY, ItemActionBlockEntity::tick);
    }


}
