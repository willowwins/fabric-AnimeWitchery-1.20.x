package net.willowins.animewitchery.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.willowins.animewitchery.block.ModBlocks;

public class RosewillowMegaTreeFeature extends Feature<DefaultFeatureConfig> {
    public RosewillowMegaTreeFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        // Safety check
        BlockState down = world.getBlockState(origin.down());
        if (!down.isIn(net.minecraft.registry.tag.BlockTags.DIRT))
            return false;

        // 10% chance to generate a Rosewillow Bulb under the tree
        if (random.nextInt(10) == 0) {
            safeSetBlock(world, origin.down(), ModBlocks.ROSEWILLOW_BULB.getDefaultState(), origin);
        }

        // SCALE UP: Height increased ~20% (was 22+6 -> 27+8)
        int height = 27 + random.nextInt(8);

        // 1. Trunk (Wider + Organic Taper)
        for (int y = 0; y < height - 2; y++) {
            double radius = 2.5; // Slightly wider base trunk

            // Organic Taper (Concave Flare)
            if (y < 10) {
                double progress = (10 - y) / 10.0;
                radius += 3.5 * progress * progress;
                radius += (random.nextFloat() - 0.5) * 0.2;
            }

            generateCircle(world, origin.add(0, y, 0), radius, ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
        }

        // 2. Roots (More extensive to match scale)
        generateRoots(world, origin, random, origin);

        // 3. Cohesive Branches (Scaled Up)
        int branchCount = 18 + random.nextInt(6);
        int startY = height / 2;

        for (int i = 0; i < branchCount; i++) {
            float angle = random.nextFloat() * 6.28f;
            int y = startY + random.nextInt(height - startY - 2);
            float relativeHeight = (float) (y - startY) / (float) (height - startY);

            float pitch = -0.1f + (relativeHeight * 0.9f);

            int length = 12 + random.nextInt(8);
            length = (int) (length * (1.2f - (relativeHeight * 0.5f)));

            generateBranchWithLeaves(world, origin.up(y), angle, pitch, length, random, origin);
        }

        // Top cap
        generateCanopyCluster(world, origin.up(height), 6, random, origin);

        return true;
    }

    private void generateCircle(StructureWorldAccess world, BlockPos center, double radius, BlockState state,
            BlockPos origin) {
        int r = (int) Math.ceil(radius);
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                if ((x + 0.5) * (x + 0.5) + (z + 0.5) * (z + 0.5) <= radius * radius) {
                    BlockPos logPos = center.add(x, 0, z);
                    safeSetBlock(world, logPos, state, origin);

                    // Convert ground below trunk to roots
                    BlockPos groundPos = logPos.down();
                    if (world.getBlockState(groundPos).isIn(net.minecraft.registry.tag.BlockTags.DIRT)) {
                        safeSetBlock(world, groundPos, ModBlocks.ROSEWILLOW_ROOTS.getDefaultState(), origin);
                    }
                }
            }
        }
    }

    private void generateBranchWithLeaves(StructureWorldAccess world, BlockPos start, float angle, float pitch,
            int length, Random random, BlockPos origin) {
        double dx = Math.cos(angle);
        double dz = Math.sin(angle);
        double dy = Math.tan(pitch);

        // Normalize
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= dist;
        dy /= dist;
        dz /= dist;

        for (int i = 0; i < length; i++) {
            BlockPos next = start.add((int) (dx * i), (int) (dy * i), (int) (dz * i));

            safeSetBlock(world, next, ModBlocks.ROSEWILLOW_LOG.getDefaultState()
                    .with(net.minecraft.block.PillarBlock.AXIS, Direction.Axis.X), origin);

            // Leaves
            if (i > length * 0.3) {
                int radius = 4 + (int) ((float) (i - length * 0.3) / (length * 0.7) * 3);
                if (random.nextInt(3) == 0) {
                    generateCanopyCluster(world, next, radius, random, origin);
                }
            }

            // Tip Cluster
            if (i == length - 1) {
                generateCanopyCluster(world, next, 8, random, origin);
            }
        }
    }

    private void safeSetBlock(StructureWorldAccess world, BlockPos pos, BlockState state, BlockPos origin) {
        // Enforce 3x3 chunk safety check (Center chunk + 1 neighbor)
        // If outside this range, the world is likely unloaded, causing "Far Chunk"
        // errors.
        int originChunkX = origin.getX() >> 4;
        int originChunkZ = origin.getZ() >> 4;
        int targetChunkX = pos.getX() >> 4;
        int targetChunkZ = pos.getZ() >> 4;

        if (Math.abs(originChunkX - targetChunkX) > 1 || Math.abs(originChunkZ - targetChunkZ) > 1) {
            return; // Skip placement to avoid error (and avoid Crashing/Lag)
        }

        if (world.isAir(pos) || world.getBlockState(pos).isIn(net.minecraft.registry.tag.BlockTags.LEAVES)
                || world.getBlockState(pos).isIn(net.minecraft.registry.tag.BlockTags.SAPLINGS)
                || world.getBlockState(pos).isOf(ModBlocks.ROSEWILLOW_VINES)
                || world.getBlockState(pos).isOf(ModBlocks.ROSEWILLOW_VINES_TIP)) {

            // Randomly swap log for blooming log if it is a log
            if (state.isOf(ModBlocks.ROSEWILLOW_LOG)) {
                if (world.getRandom().nextInt(5) == 0) { // 20% chance
                    state = ModBlocks.ROSEWILLOW_LOG_BLOOMING.getDefaultState()
                            .with(net.minecraft.block.PillarBlock.AXIS,
                                    state.get(net.minecraft.block.PillarBlock.AXIS));
                }
            }

            world.setBlockState(pos, state, 3);
        }
    }

    // Helper to generate canopy sphere
    private void generateCanopyCluster(StructureWorldAccess world, BlockPos center, int radius, Random random,
            BlockPos origin) {
        // Support logs for large clusters to prevent decay (Distance > 7 decays)
        if (radius > 5) {
            int offset = radius / 2;
            safeSetBlock(world, center.add(offset, 0, 0), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
            safeSetBlock(world, center.add(-offset, 0, 0), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
            safeSetBlock(world, center.add(0, 0, offset), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
            safeSetBlock(world, center.add(0, 0, -offset), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
            safeSetBlock(world, center.add(0, offset, 0), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
        }

        for (int y = -radius / 2; y <= radius / 2; y++) {
            int currentRadius = radius - Math.abs(y);
            for (int x = -currentRadius; x <= currentRadius; x++) {
                for (int z = -currentRadius; z <= currentRadius; z++) {
                    if (x * x + z * z <= currentRadius * currentRadius) {
                        BlockPos leafPos = center.add(x, y, z);
                        if (world.isAir(leafPos)) {
                            safeSetBlock(world, leafPos, ModBlocks.ROSEWILLOW_LEAVES.getDefaultState()
                                    .with(net.minecraft.block.LeavesBlock.PERSISTENT, false)
                                    .with(net.minecraft.block.LeavesBlock.DISTANCE, 1), origin);

                            // Vines logic - Only at edge/bottom
                            if (y < -radius / 3 && random.nextInt(6) == 0) {
                                generateVine(world, leafPos.down(), random, origin);
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateRoots(StructureWorldAccess world, BlockPos pos, Random random, BlockPos origin) {
        for (int i = 0; i < 8; i++) {
            float angle = random.nextFloat() * 6.28f;
            int dist = 3 + random.nextInt(3);
            int dx = (int) (Math.cos(angle) * dist);
            int dz = (int) (Math.sin(angle) * dist);
            BlockPos rootPos = pos.add(dx, 0, dz);

            if (world.isAir(rootPos))
                safeSetBlock(world, rootPos, ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
            if (random.nextBoolean() && world.isAir(rootPos.down()))
                safeSetBlock(world, rootPos.down(), ModBlocks.ROSEWILLOW_LOG.getDefaultState(), origin);
        }
    }

    private void generateVine(StructureWorldAccess world, BlockPos pos, Random random, BlockPos origin) {
        if (!world.isAir(pos))
            return;

        int length = 3 + random.nextInt(10);
        for (int i = 0; i < length; i++) {
            BlockPos vinePos = pos.down(i);
            if (!world.isAir(vinePos))
                break;

            if (i == length - 1) {
                // Tip is Blossom
                safeSetBlock(world, vinePos, ModBlocks.LARGE_ROSEWILLOW_BLOSSOM.getDefaultState(), origin);
            } else if (i == length - 2) {
                safeSetBlock(world, vinePos, ModBlocks.ROSEWILLOW_VINES_TIP.getDefaultState(), origin);
            } else {
                // Body (Blooming chance)
                BlockState vineState = ModBlocks.ROSEWILLOW_VINES.getDefaultState();
                if (random.nextInt(3) == 0) {
                    vineState = vineState.with(net.minecraft.state.property.Properties.BERRIES, true);
                }
                safeSetBlock(world, vinePos, vineState, origin);
            }
        }
    }
}
