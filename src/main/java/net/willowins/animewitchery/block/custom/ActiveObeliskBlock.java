package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ActiveObeliskBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public ActiveObeliskBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        // Spawn particles around the active obelisk
        spawnActiveParticles(world, pos);
        
        // Schedule next tick
        world.scheduleBlockTick(pos, this, 20); // Every second
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        // If this obelisk is part of an active ritual, notify the circle to deactivate
        if (!world.isClient()) {
            // Check for nearby barrier circles that might have active rituals
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos checkPos = pos.add(x, 0, z);
                    BlockState checkState = world.getBlockState(checkPos);
                    
                    if (checkState.isOf(ModBlocks.BARRIER_CIRCLE)) {
                        BlockEntity blockEntity = world.getBlockEntity(checkPos);
                        if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
                            if (circleEntity.isRitualActive()) {
                                // Check if this obelisk is one of the ritual obelisks
                                BlockPos circlePos = circleEntity.getPos();
                                BlockPos northPos = circlePos.north(5);
                                BlockPos southPos = circlePos.south(5);
                                BlockPos eastPos = circlePos.east(5);
                                BlockPos westPos = circlePos.west(5);
                                
                                if (pos.equals(northPos) || pos.equals(southPos) || 
                                    pos.equals(eastPos) || pos.equals(westPos)) {
                                    System.out.println("ActiveObelisk: Obelisk broken during active ritual - deactivating ritual!");
                                    circleEntity.deactivateRitual();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS);
        super.onBroken(world, pos, state);
    }

    private void spawnActiveParticles(ServerWorld world, BlockPos pos) {
        // Spawn particles around the active obelisk
        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 3;
            double y = pos.getY() + 1 + world.getRandom().nextDouble() * 3;
            double z = pos.getZ() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 3;
            
            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0.05);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.ACTIVE_OBELISK_BLOCK_ENTITY, ActiveObeliskBlockEntity::tick);
    }
} 