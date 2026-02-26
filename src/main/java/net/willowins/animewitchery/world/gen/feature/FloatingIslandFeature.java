package net.willowins.animewitchery.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.willowins.animewitchery.AnimeWitchery;

public class FloatingIslandFeature extends Feature<DefaultFeatureConfig> {
    public FloatingIslandFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    // Simple value noise — returns -1.0 to 1.0
    private double valueNoise(double x, double z, long seed) {
        long ix = (long) Math.floor(x);
        long iz = (long) Math.floor(z);
        double fx = x - Math.floor(x);
        double fz = z - Math.floor(z);
        // Smooth
        fx = fx * fx * (3 - 2 * fx);
        fz = fz * fz * (3 - 2 * fz);

        double v00 = hash(ix, iz, seed);
        double v10 = hash(ix + 1, iz, seed);
        double v01 = hash(ix, iz + 1, seed);
        double v11 = hash(ix + 1, iz + 1, seed);

        return v00 + (v10 - v00) * fx
                + (v01 - v00) * fz
                + (v00 - v10 - v01 + v11) * fx * fz;
    }

    private double hash(long x, long z, long seed) {
        long h = seed ^ (x * 0x9E3779B97F4A7C15L) ^ (z * 0x6C62272E07BB0142L);
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        h ^= h >>> 31;
        return (h & 0xFFFFFFFFL) / (double) 0xFFFFFFFFL * 2.0 - 1.0;
    }

    // Fractal noise (3 octaves)
    private double fbm(double x, double z, long seed) {
        double v = 0;
        double amp = 1.0, freq = 1.0, max = 0;
        for (int i = 0; i < 3; i++) {
            v += valueNoise(x * freq, z * freq, seed + i * 12345L) * amp;
            max += amp;
            amp *= 0.5;
            freq *= 2.0;
        }
        return v / max;
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();
        BlockPos origin = context.getOrigin();

        ChunkPos chunkPos = new ChunkPos(origin);
        // Safe write bounds: chunk ± 8 blocks
        int safeMinX = chunkPos.getStartX() - 8;
        int safeMaxX = chunkPos.getEndX() + 8;
        int safeMinZ = chunkPos.getStartZ() - 8;
        int safeMaxZ = chunkPos.getEndZ() + 8;

        // Use a seed derived from the origin for deterministic noise
        long noiseSeed = (long) origin.getX() * 341873128712L ^ (long) origin.getZ() * 132897987541L
                ^ random.nextLong();

        // Island parameters — radius kept within safe bounds
        int radius = 10 + random.nextInt(5); // 10–14 blocks (fits in ±8 margin)

        // Multi-lobe shape: 3–5 overlapping ellipses offset from center
        int numLobes = 3 + random.nextInt(3);
        double[] lobeOffX = new double[numLobes];
        double[] lobeOffZ = new double[numLobes];
        double[] lobeRadX = new double[numLobes];
        double[] lobeRadZ = new double[numLobes];
        for (int l = 0; l < numLobes; l++) {
            double angle = (l / (double) numLobes) * Math.PI * 2 + random.nextDouble() * 0.8;
            double dist = random.nextDouble() * radius * 0.45;
            lobeOffX[l] = Math.cos(angle) * dist;
            lobeOffZ[l] = Math.sin(angle) * dist;
            lobeRadX[l] = radius * (0.55 + random.nextDouble() * 0.55);
            lobeRadZ[l] = radius * (0.55 + random.nextDouble() * 0.55);
        }

        int bodyHeight = 20 + random.nextInt(14); // 20–33 blocks thick

        BlockState stone = Blocks.STONE.getDefaultState();
        BlockState dirt = Blocks.DIRT.getDefaultState();
        BlockState grass = Blocks.GRASS_BLOCK.getDefaultState();
        BlockState cobble = Blocks.COBBLESTONE.getDefaultState();
        BlockState deepslate = Blocks.DEEPSLATE.getDefaultState();

        BlockState starlight = Registries.BLOCK
                .get(new Identifier(AnimeWitchery.MOD_ID, "starlight_block"))
                .getDefaultState();
        if (starlight.isAir())
            starlight = Blocks.WATER.getDefaultState();

        // --- 1. Generate island body ---
        for (int x = -radius - 2; x <= radius + 2; x++) {
            for (int z = -radius - 2; z <= radius + 2; z++) {
                int worldX = origin.getX() + x;
                int worldZ = origin.getZ() + z;
                if (worldX < safeMinX || worldX > safeMaxX || worldZ < safeMinZ || worldZ > safeMaxZ)
                    continue;

                // Compute multi-lobe density: max over all lobes
                double lobeDensity = 0;
                for (int l = 0; l < numLobes; l++) {
                    double dx = (x - lobeOffX[l]) / lobeRadX[l];
                    double dz = (z - lobeOffZ[l]) / lobeRadZ[l];
                    double d = Math.sqrt(dx * dx + dz * dz);
                    lobeDensity = Math.max(lobeDensity, 1.0 - d);
                }

                // Add noise warp for irregular edges
                double noiseScale = 0.18;
                double edgeNoise = fbm(worldX * noiseScale, worldZ * noiseScale, noiseSeed) * 0.35;
                double density = lobeDensity + edgeNoise;

                if (density <= 0)
                    continue;

                // Top surface: slight noise variation
                double topNoise = fbm(worldX * 0.25, worldZ * 0.25, noiseSeed + 99L);
                int topY = (int) (topNoise * 2); // -2 to +2

                // Bottom: curved taper with noise for jagged underside
                double bottomNoise = fbm(worldX * 0.22, worldZ * 0.22, noiseSeed + 777L);
                double curve = Math.pow(density, 0.7);
                int bottomY = -(int) (bodyHeight * curve) - (int) (bottomNoise * 4 + 2);

                for (int y = bottomY; y <= topY; y++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (y == topY) {
                        world.setBlockState(pos, grass, 3);
                    } else if (y >= topY - 3) {
                        world.setBlockState(pos, dirt, 3);
                    } else if (y <= bottomY + 3) {
                        // Jagged bottom: mix deepslate and cobble
                        world.setBlockState(pos, random.nextInt(3) == 0 ? deepslate : cobble, 3);
                    } else {
                        world.setBlockState(pos, stone, 3);
                    }
                }

                // Stalactites hanging from the bottom (irregular spikes)
                if (density > 0.2 && random.nextInt(5) == 0) {
                    int stalLen = 2 + random.nextInt(6);
                    for (int s = 1; s <= stalLen; s++) {
                        BlockPos sp = origin.add(x, bottomY - s, z);
                        if (sp.getY() < world.getBottomY())
                            break;
                        world.setBlockState(sp, s <= 2 ? cobble : deepslate, 3);
                    }
                }
            }
        }

        // --- 2. Starlight pools on top ---
        int numPools = 1 + random.nextInt(3);
        for (int i = 0; i < numPools; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double poolDist = random.nextDouble() * radius * 0.4;
            int px = (int) (Math.cos(angle) * poolDist);
            int pz = (int) (Math.sin(angle) * poolDist);
            int poolRadius = 2 + random.nextInt(3);

            for (int dx = -poolRadius; dx <= poolRadius; dx++) {
                for (int dz = -poolRadius; dz <= poolRadius; dz++) {
                    int worldX = origin.getX() + px + dx;
                    int worldZ = origin.getZ() + pz + dz;
                    if (worldX < safeMinX || worldX > safeMaxX || worldZ < safeMinZ || worldZ > safeMaxZ)
                        continue;
                    if (dx * dx + dz * dz <= poolRadius * poolRadius) {
                        BlockPos poolPos = origin.add(px + dx, 0, pz + dz);
                        world.setBlockState(poolPos, starlight, 3);
                        world.setBlockState(poolPos.down(), starlight, 3);
                        world.setBlockState(poolPos.down(2), stone, 3);
                        BlockState above = world.getBlockState(poolPos.up());
                        if (above.isOf(Blocks.GRASS_BLOCK) || above.isOf(Blocks.DIRT)) {
                            world.setBlockState(poolPos.up(), Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }

            // Overflow drip off edge
            int stepX = (int) Math.signum(px);
            int stepZ = (int) Math.signum(pz);
            if (stepX == 0 && stepZ == 0)
                stepX = random.nextBoolean() ? 1 : -1;
            BlockPos walker = origin.add(px, 0, pz);
            for (int step = 0; step < radius + 4; step++) {
                walker = walker.add(stepX, 0, stepZ);
                int wx = walker.getX(), wz = walker.getZ();
                if (wx < safeMinX || wx > safeMaxX || wz < safeMinZ || wz > safeMaxZ)
                    break;
                if (world.isAir(walker) || world.isAir(walker.down())) {
                    world.setBlockState(walker, starlight, 3);
                    break;
                }
                world.setBlockState(walker, starlight, 3);
                world.setBlockState(walker.up(), Blocks.AIR.getDefaultState(), 3);
            }
        }

        return true;
    }
}
