package net.willowins.animewitchery.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.willowins.animewitchery.block.ModBlocks;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RosewillowRootsBlock extends Block {
    public RosewillowRootsBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Requirement: Roots don't grow unless there's a bulb in the system.
        if (!isConnectedToBulb(world, pos)) {
            return;
        }

        if (random.nextInt(5) == 0) { // 20% chance to spread or grow
            grow(world, pos, random, false);
        }
    }

    private boolean isConnectedToBulb(ServerWorld world, BlockPos startPos) {
        // BFS to find a bulb.
        // Removed distance limit as per user request for "infinite" growth.
        // Added a safety limit to visited nodes to prevent complete thread locking on
        // massive networks.
        int visitedLimit = 10000;
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        queue.add(startPos);
        visited.add(startPos);

        int nodesVisited = 0;

        while (!queue.isEmpty()) {
            if (nodesVisited++ > visitedLimit) {
                return false; // Assume disconnected if not found within limit to prevent lag
            }

            BlockPos current = queue.poll();
            if (world.getBlockState(current).isOf(ModBlocks.ROSEWILLOW_BULB)) {
                return true;
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.offset(dir);
                if (!visited.contains(neighbor)) {
                    BlockState neighborState = world.getBlockState(neighbor);
                    // Can traverse through Roots or Bulb
                    if (neighborState.isOf(ModBlocks.ROSEWILLOW_ROOTS)
                            || neighborState.isOf(ModBlocks.ROSEWILLOW_BULB)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return false;
    }

    void grow(ServerWorld world, BlockPos pos, Random random, boolean force) {
        BlockPos above = pos.up();
        // Check for surface condition (Reach Air above, planted on valid soil/roots)
        if (world.getBlockState(above).isAir() && canPlantOn(world, pos)) {

            // Proximity Check: Don't plant saplings if too close to an existing tree
            if (!hasNearbySource(world, pos, 20)) {
                // Try to plant 2x2 saplings
                // Iterate through corners to find valid 2x2 spot including this pos
                boolean planted = false;
                for (int x = -1; x <= 0; x++) {
                    for (int z = -1; z <= 0; z++) {
                        if (canPlant2x2(world, pos.add(x, 0, z))) {
                            plant2x2(world, pos.add(x, 0, z));
                            planted = true;
                            break;
                        }
                    }
                    if (planted)
                        break;
                }

                // If 2x2 failed, plant single sapling
                if (!planted) {
                    world.setBlockState(above, ModBlocks.ROSEWILLOW_SAPLING.getDefaultState());
                }
            } // End proximity check

            // Do NOT return here. Continue to spread logic.
        }

        // Spread logic: Bias upwards and outwards
        if (!force && random.nextInt(3) == 0)
            return; // Reduce spread rate for natural growth

        // If forced, try to be smarter about finding a valid spot
        if (force) {
            // Iterate all neighbors?
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0)
                            continue;
                        BlockPos target = pos.add(dx, dy, dz);
                        if (isValidSoil(world.getBlockState(target))) {

                            if (getRootNeighbors(world, target) < 2) { // Density check: Max 1 existing root neighbor
                                                                       // (the parent)
                                world.setBlockState(target, this.getDefaultState());
                                return; // Found a spot, done.
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < 3; i++) { // Try 3 times
                // -1 to 1 X/Z, 0 to 1 Y (prefer up)
                BlockPos target = pos.add(random.nextInt(3) - 1, random.nextInt(2), random.nextInt(3) - 1);
                if (target.equals(pos))
                    continue;

                if (isValidSoil(world.getBlockState(target))) {

                    if (getRootNeighbors(world, target) < 2) {
                        world.setBlockState(target, this.getDefaultState());
                        return; // Spread once per tick
                    }
                }
            }
        }
    }

    private boolean hasNearbySource(ServerWorld world, BlockPos pos, int radius) {
        // Optimize: Check a flattened cylinder, mostly looking up for logs, or around
        // for saplings
        // However, user requested 35 block radius.
        // To prevent lag, we might limit the vertical search or step size??
        // Let's do a naive search but limit verticality a bit less than full 35 if
        // reasonable,
        // but trees can be tall. 35 is safe.

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -10; y <= 35; y++) { // Look mostly up (tree) and slightly down (roots/sapling nearby)
                for (int z = -radius; z <= radius; z++) {
                    mutable.set(pos, x, y, z);
                    // Distance check (squared)
                    if (mutable.getSquaredDistance(pos) > radius * radius)
                        continue;

                    BlockState state = world.getBlockState(mutable);
                    if (state.isOf(ModBlocks.ROSEWILLOW_SAPLING)
                            || state.isOf(ModBlocks.ROSEWILLOW_LOG)
                            || state.isOf(ModBlocks.ROSEWILLOW_LOG_BLOOMING)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getRootNeighbors(ServerWorld world, BlockPos pos) {
        int count = 0;
        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).isOf(this)) {
                count++;
            }
        }
        return count;
    }

    private boolean canPlantOn(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isIn(net.minecraft.registry.tag.BlockTags.DIRT) || state.isOf(Blocks.GRASS_BLOCK)
                || state.isOf(ModBlocks.ROSEWILLOW_ROOTS);
    }

    private boolean isValidSoil(BlockState state) {
        return state.isIn(net.minecraft.registry.tag.BlockTags.DIRT) || state.isOf(Blocks.STONE)
                || state.isOf(Blocks.DEEPSLATE) || state.isOf(Blocks.GRAVEL) || state.isOf(Blocks.GRASS_BLOCK);
    }

    private boolean canPlant2x2(ServerWorld world, BlockPos corner) {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                BlockPos p = corner.add(x, 0, z);
                BlockPos up = p.up();
                if (!world.getBlockState(up).isAir())
                    return false;
                if (!canPlantOn(world, p))
                    return false;
            }
        }
        return true;
    }

    private void plant2x2(ServerWorld world, BlockPos corner) {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                world.setBlockState(corner.add(x, 1, z), ModBlocks.ROSEWILLOW_SAPLING.getDefaultState());
            }
        }
    }
}
