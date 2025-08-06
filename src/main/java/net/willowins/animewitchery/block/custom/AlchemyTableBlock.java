package net.willowins.animewitchery.block.custom;


import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;


public class AlchemyTableBlock extends BlockWithEntity {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 20, 16);

    public AlchemyTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        
        // Check if player is holding alchemical catalyst
        if (heldItem.isOf(ModItems.ALCHEMICAL_CATALYST)) {
            if (!world.isClient) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof AlchemyTableBlockEntity alchemyTable) {
                    if (alchemyTable.activateWithCatalyst(player)) {
                        // Consume the catalyst
                        heldItem.decrement(1);
                        
                        // Spawn activation particles
                        if (world instanceof ServerWorld serverWorld) {
                            spawnActivationParticles(serverWorld, pos);
                        }
                        
                        // Play activation sound
                        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                        
                        return ActionResult.SUCCESS;
                    } else {
                        // Play failure sound if no valid recipe or not enough XP
                        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), SoundCategory.BLOCKS, 1.0f, 0.5f);
                        return ActionResult.FAIL;
                    }
                }
            }
            return ActionResult.SUCCESS;
        }
        
        // Otherwise, open the GUI
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }
    
    private void spawnActivationParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = Vec3d.ofCenter(pos).add(0, 1.0, 0);
        
        // Burst of portal particles
        for (int i = 0; i < 20; i++) {
            double angle = (i * Math.PI * 2) / 20;
            double radius = 2.0;
            
            double x = center.x + Math.cos(angle) * radius;
            double y = center.y + (Math.random() - 0.5) * 2.0;
            double z = center.z + Math.sin(angle) * radius;
            
            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 3, 0.2, 0.2, 0.2, 0.1);
        }
        
        // Additional sparkle effect
        for (int i = 0; i < 15; i++) {
            double x = center.x + (Math.random() - 0.5) * 3.0;
            double y = center.y + Math.random() * 2.0;
            double z = center.z + (Math.random() - 0.5) * 3.0;
            
            world.spawnParticles(ParticleTypes.ENCHANT, x, y, z, 2, 0.1, 0.1, 0.1, 0.05);
        }
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
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY.instantiate(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, ModBlockEntities.ALCHEMY_TABLE_BLOCK_ENTITY, AlchemyTableBlockEntity::tick);
    }
}


