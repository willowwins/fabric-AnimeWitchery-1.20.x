package net.willowins.animewitchery.block.custom;


import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;


public class DecorativeFountainBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public DecorativeFountainBlock(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
                if (player.getOffHandStack().isOf(ModItems.RESPAWN_BEACON)&&player.getMainHandStack().isOf(Blocks.REINFORCED_DEEPSLATE.asItem())){
                        player.getMainHandStack().decrement(1);
                        player.getOffHandStack().decrement(1);
                        Block.dropStack(world,pos,new ItemStack(ModItems.DIMENSION_HOPPER,1));
                }}
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    }


