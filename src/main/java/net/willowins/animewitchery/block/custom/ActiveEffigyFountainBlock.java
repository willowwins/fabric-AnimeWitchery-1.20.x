package net.willowins.animewitchery.block.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class ActiveEffigyFountainBlock extends Block {
    public ActiveEffigyFountainBlock(AbstractBlock.Settings settings) {
    super(settings);
}


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        asNearbyPlayers(world, 20, pos);
        world.scheduleBlockTick(pos, this, 10);

    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            world.scheduleBlockTick(pos, this, 60);
        }
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
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,300,255));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,300,255));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION,300,5));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING,300,0));
                ((ServerWorld) world).spawnParticles(ParticleTypes.ENCHANT, target.getX(), target.getY()+1, target.getZ(), 10, 0.5,0.5,0.5, 0.1);
            }
        }


    }
}
