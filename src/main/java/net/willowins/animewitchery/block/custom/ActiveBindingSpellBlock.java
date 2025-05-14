package net.willowins.animewitchery.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ModBlockEntities;
import net.willowins.animewitchery.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ActiveBindingSpellBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);

    public ActiveBindingSpellBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient){
            if(player.isHolding(ModItems.SILVER)) {
                world.setBlockState(pos, ModBlocks.BINDING_SPELL.getDefaultState());
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS);
            }else {
                world.playSound(null,pos,SoundEvents.BLOCK_SAND_STEP,SoundCategory.BLOCKS);
                }
        }

        return ActionResult.SUCCESS;}


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

            asNearbyPlayers(world, .5f, pos);

        world.scheduleBlockTick(pos, this,1);

    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS);
        super.onBroken(world, pos, state);
    }

    private void asNearbyPlayers(World world, double radius, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Box box = new Box(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );

        List<PlayerEntity> player = serverWorld.getEntitiesByClass(PlayerEntity.class, box, entity -> true);


        for (PlayerEntity target : player) {
            if (!target.getInventory().contains(Items.BEDROCK.getDefaultStack())) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,300,255,false,false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,300,255,false,false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION,300,5,false,false));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS,20,255,true,true));
                ((ServerWorld) world).spawnParticles(ParticleTypes.PORTAL, target.getX(), target.getY()+1, target.getZ(), 1, 0.5,0.5,0.5, 0.1);
            }
        }


    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.ACTIVE_BINDING_SPELL_BLOCK_ENTITY.instantiate(pos, state);
    }
}
