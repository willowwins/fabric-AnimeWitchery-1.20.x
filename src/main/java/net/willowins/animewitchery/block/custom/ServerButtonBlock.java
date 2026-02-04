package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.ServerButtonBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ServerButtonBlock extends BlockWithEntity {

    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 2.0, 11.0);

    public ServerButtonBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ServerButtonBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ServerButtonBlockEntity blockEntity) {
            net.minecraft.item.ItemStack heldItem = player.getStackInHand(hand);

            // Nautilus Shell = Open multiplayer screen
            if (heldItem.isOf(net.minecraft.item.Items.NAUTILUS_SHELL)) {
                if (world.isClient) {
                    net.willowins.animewitchery.client.ClientBlockUtil.openMultiplayerScreen();
                }
                return ActionResult.SUCCESS;
            }

            // Stick = Cycle through servers
            if (heldItem.isOf(net.minecraft.item.Items.STICK)) {
                if (world.isClient) {
                    net.willowins.animewitchery.client.ClientBlockUtil.cycleServerClientSide(pos);
                }
                return ActionResult.success(world.isClient);
            }

            // Normal click = Connect to server directly
            String serverIP = blockEntity.getServerIP();
            String serverName = blockEntity.getServerName();
            if (serverIP != null && !serverIP.isEmpty()) {
                if (world.isClient) {
                    System.out.println("[ServerButton] Button clicked, connecting to: " + serverIP);
                    net.willowins.animewitchery.client.ClientBlockUtil.connectToServerImmediately(serverName, serverIP);
                }
                return ActionResult.SUCCESS;
            } else {
                if (!world.isClient) {
                    player.sendMessage(Text.literal("§7Right-click with stick to cycle"), true);
                }
            }
        }
        return ActionResult.success(world.isClient);
    }
}
