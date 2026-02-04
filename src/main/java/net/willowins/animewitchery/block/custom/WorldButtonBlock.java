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
import net.willowins.animewitchery.block.entity.WorldButtonBlockEntity;
import org.jetbrains.annotations.Nullable;

public class WorldButtonBlock extends BlockWithEntity {

    // Use a flat shape like a pressure plate or button
    // This is a thin box on the floor: x=5-11, y=0-2, z=5-11
    protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 2.0, 11.0);

    public WorldButtonBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WorldButtonBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
            BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof WorldButtonBlockEntity blockEntity) {
            net.minecraft.item.ItemStack heldItem = player.getStackInHand(hand);

            // Nautilus Shell = Open world selections
            if (heldItem.isOf(net.minecraft.item.Items.NAUTILUS_SHELL)) {
                if (world.isClient) {
                    net.willowins.animewitchery.client.ClientBlockUtil.openWorldSelectScreen();
                }
                return ActionResult.SUCCESS;
            }

            // Stick = Cycle through worlds
            if (heldItem.isOf(net.minecraft.item.Items.STICK)) {
                if (world.isClient) {
                    net.willowins.animewitchery.client.ClientBlockUtil.cycleWorldClientSide(pos);
                }
                return ActionResult.success(world.isClient);
            }

            // Normal click = Load world
            String worldName = blockEntity.getWorldName();
            if (worldName != null && !worldName.isEmpty()) {
                if (world.isClient) {
                    System.out.println("[WorldButton] Button clicked, loading world: " + worldName);
                    net.willowins.animewitchery.client.ClientBlockUtil.loadWorldImmediately(worldName);
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
