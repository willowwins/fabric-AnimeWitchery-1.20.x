package net.willowins.animewitchery.block.custom;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ParticleBeamBlock extends Block {
    public ParticleBeamBlock() {
        super(FabricBlockSettings.copyOf(Blocks.AMETHYST_BLOCK).strength(4.0f, 6.0f)); // Choose material and strength
    }

    // Called when redstone or nearby block changes
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {

        if (!(world instanceof ServerWorld serverWorld)) return;
        if (world.isReceivingRedstonePower(pos)) {
            // Start ticking if powered
            world.scheduleBlockTick(pos, this, 2);
        }
    }
    // Called every tick while scheduled
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isReceivingRedstonePower(pos)) {
            spawnBeamParticles(world, pos);
            spawnSpiralParticles(world, pos);
            Box effectZone = new Box(pos).expand(1, 20, 1).offset(0.5, 0, 0.5); // centered on block

            // Apply upward force to entities in the effect zone
            for (Entity entity : world.getEntitiesByClass(LivingEntity.class, effectZone, e -> true)) {
                entity.setVelocity(entity.getVelocity().x, 0.3, entity.getVelocity().z); // Adjust the upward force as needed
                entity.velocityModified = true;
            }

            // Schedule next tick if still powered
            world.scheduleBlockTick(pos, this, 2); // delay = 2 ticks (~0.1s)
        }
    }



    private void spawnBeamParticles(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        int beamHeight = 100;

        for (int i = 0; i < beamHeight; i++) {
            double yOffset = y + i * 0.2;
            world.spawnParticles(ParticleTypes.PORTAL, x, yOffset, z,
                    1, // count
                    0.05, 0.05, 0.05, // spread (adds visibility)
                    0.0); // speed
        }
    }

    private void spawnSpiralParticles(ServerWorld world, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.0;
        double z = pos.getZ() + 0.5;

        int spiralLength = 200;
        double time = world.getTime();
        double spinSpeed = -0.05;

        for (int i = 0; i < spiralLength; i++) {
            double baseAngle = (i * Math.PI * 2) / 50;
            double angle = baseAngle + time * spinSpeed;
            double radius = 1 + Math.sin(i * 0.1) * 0.5;

            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);
            double yOffset = y + i * 0.1;

            world.spawnParticles(ParticleTypes.REVERSE_PORTAL, x + xOffset, yOffset, z + zOffset,
                    1, // count
                    0.02, 0.02, 0.02, // slight spread
                    0.0); // speed
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, net.minecraft.entity.Entity entity) {
        super.onSteppedOn(world, pos, state, entity);

        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            if (entity instanceof LivingEntity) {
                // Define a box centered on the block, 1 block high
                Box liftZone = new Box(pos).expand(0.4, 0.4, 0.4).offset(0.5, 0, 0.5);

                // Check if the entity is inside the lift zone
                if (liftZone.contains(entity.getPos())) {
                    // Apply upward motion
                    entity.setVelocity(entity.getVelocity().x, 0.1, entity.getVelocity().z); // Adjust the upward force as needed
                    entity.velocityModified = true;
                }
            }
        }
    }
}