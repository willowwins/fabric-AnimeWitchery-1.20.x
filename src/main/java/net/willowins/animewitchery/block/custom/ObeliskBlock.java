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
import net.willowins.animewitchery.sound.ModSounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ObeliskBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 32, 16);

    public ObeliskBlock(Settings settings) {
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (player.isHolding(Items.DIAMOND)) {
                world.setBlockState(pos, ModBlocks.ACTIVE_OBELISK.getDefaultState());
                
                // Spawn activation particles
                if (world instanceof ServerWorld serverWorld) {
                    spawnActivationParticles(serverWorld, pos);
                }
                
                // Play activation sound
                world.playSound(null, pos, ModSounds.OBELISK_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 80);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS);
        super.onBroken(world, pos, state);
    }

    private void spawnActivationParticles(ServerWorld world, BlockPos pos) {
        // Spawn a burst of particles around the obelisk
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 2;
            double y = pos.getY() + 1 + world.getRandom().nextDouble() * 2;
            double z = pos.getZ() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 2;
            
            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 1, 0, 0, 0, 0.1);
            world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0, 0, 0, 0.1);
        }
        
        // Summon lightning on the obelisk!
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.setPosition(pos.getX() + 0.5, pos.getY() + 3, pos.getZ() + 0.5);
            world.spawnEntity(lightning);
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.OBELISK_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OBELISK_BLOCK_ENTITY, ObeliskBlockEntity::tick);
    }
} 