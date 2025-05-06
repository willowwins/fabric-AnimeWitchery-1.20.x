package net.willowins.animewitchery.block.custom;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class EffigyFountainBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public EffigyFountainBlock(Settings settings) {
        super(settings);
    }

    public static BlockPos lastEffigyPos = null;
    public static boolean active = false;
    public static int ticks = 0;
    public static World effigyworld = null;

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            if (!world.isClient) {
                if (!active) {
                    active = true;
                    ticks = 0;
                    lastEffigyPos = pos;
                    effigyworld = world;
                    if (player.getStackInHand(hand).isOf(Items.NETHER_STAR)) {
                        EntityType.LIGHTNING_BOLT.spawn((ServerWorld) world, pos, SpawnReason.EVENT);
                        player.sendMessage(Text.literal("A NEW HAND TOUCHES THE BEACON"), true);
                        world.setBlockState(pos, ModBlocks.ACTIVE_EFFIGY_FOUNTAIN.getDefaultState());
                        player.damage(player.getDamageSources().cramming(), 2);
                        player.getStackInHand(hand).decrement(1);
                        player.giveItemStack(new ItemStack(ModItems.METAL_DETECTOR, 1));
                    } else {
                        EntityType.LIGHTNING_BOLT.spawn((ServerWorld) world, pos, SpawnReason.EVENT);
                        player.sendMessage(Text.literal("A NEW HAND TOUCHES THE BEACON"), true);
                        world.setBlockState(pos, ModBlocks.ACTIVE_EFFIGY_FOUNTAIN.getDefaultState());
                        player.damage(player.getDamageSources().cramming(), 2);
                    }
                } else {
                    if (!world.isClient) {
                        player.sendMessage(Text.literal("Please wait, a fountain is currently active"));
                        player.sendMessage(Text.literal(ticks/20 + "/" + 3600), true);
                    }
                }
            }

        return ActionResult.SUCCESS;
    }}


