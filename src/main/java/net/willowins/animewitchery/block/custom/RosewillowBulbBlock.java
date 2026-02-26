package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.willowins.animewitchery.block.ModBlocks;

public class RosewillowBulbBlock extends Block implements net.minecraft.block.Fertilizable {
    public RosewillowBulbBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(10) == 0) { // 10% chance to try spreading roots
            spreadRoots(world, pos, random);
        }
    }

    @Override
    public boolean isFertilizable(net.minecraft.world.WorldView world, BlockPos pos, BlockState state,
            boolean isClient) {
        return true;
    }

    @Override
    public boolean canGrow(net.minecraft.world.World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        // Stimulate root growth:
        // 1. Try to spawn new roots directly adjacent
        spreadRoots(world, pos, random);

        // 2. Propagate a "growth pulse" to existing roots to extend the network
        // Random walk to find a root tip. Increased to 20 pulses to ensure good
        // coverage.
        for (int i = 0; i < 20; i++) {
            propagateGrowth(world, pos, random);
        }
    }

    private void propagateGrowth(ServerWorld world, BlockPos startPos, Random random) {
        // "Guided Pulse" / "Growth Ray" logic
        // 1. Pick a random target destination far away
        int range = 64;
        BlockPos targetPos = startPos.add(
                random.nextInt(range * 2) - range,
                random.nextInt(range * 2) - range,
                random.nextInt(range * 2) - range);

        BlockPos currentPos = startPos;
        java.util.Set<BlockPos> visitedInPulse = new java.util.HashSet<>();
        visitedInPulse.add(startPos);

        // 2. Walk along existing roots towards that target
        for (int step = 0; step < range + 10; step++) { // Allow a bit more pathfinding
            BlockPos bestNeighbor = null;
            double bestDistSq = Double.MAX_VALUE;

            // Check all neighbors
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = currentPos.offset(dir);

                // Must be a root and not visited
                if (world.getBlockState(neighbor).isOf(ModBlocks.ROSEWILLOW_ROOTS)
                        && !visitedInPulse.contains(neighbor)) {
                    double distSq = neighbor.getSquaredDistance(targetPos);

                    // "Fuzz" the distance slightly to add randomness/organic feel so it doesn't
                    // take strict straight lines
                    // random.nextDouble() adds 0.0 to 1.0.
                    // This means a slightly worse step might be picked if the difference is small.
                    // Actually, let's keep it simple: strict hill climbing towards target.
                    // Maybe just shuffle checking order to break ties randomly.
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        bestNeighbor = neighbor;
                    }
                }
            }

            if (bestNeighbor != null) {
                currentPos = bestNeighbor;
                visitedInPulse.add(currentPos);

                // If we reached the target (unlikely but possible), stop
                if (currentPos.equals(targetPos))
                    break;
            } else {
                // No valid root path closer to target (or dead end)
                // We have reached the "tip" of this ray
                break;
            }
        }

        // 3. Force growth at the tip
        if (world.getBlockState(currentPos).isOf(ModBlocks.ROSEWILLOW_ROOTS)) {
            BlockState rootState = world.getBlockState(currentPos);
            if (rootState.getBlock() instanceof RosewillowRootsBlock rootsBlock) {
                // Force growth!
                rootsBlock.grow(world, currentPos, random, true);
            }
        }
    }

    private void spreadRoots(ServerWorld world, BlockPos pos, Random random) {
        // Simple logic: Try to place roots in adjacent blocks or extend existing roots
        // ideally moving outwards and upwards
        // For now, let's just try to place a root above or to the side

        for (int i = 0; i < 3; i++) { // Try up to 3 directions
            Direction dir = Direction.random(random);
            BlockPos targetPos = pos.offset(dir);
            BlockState targetState = world.getBlockState(targetPos);
            if (targetState.isIn(net.minecraft.registry.tag.BlockTags.DIRT)
                    || targetState.isOf(Blocks.STONE)
                    || targetState.isOf(Blocks.DEEPSLATE)
                    || targetState.isOf(Blocks.GRAVEL)
                    || targetState.isOf(Blocks.GRASS_BLOCK)) {
                world.setBlockState(targetPos, ModBlocks.ROSEWILLOW_ROOTS.getDefaultState());
                return; // Spread once per call
            }
        }
    }
}
