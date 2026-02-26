package net.willowins.animewitchery.world.gen.foliage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.willowins.animewitchery.world.gen.ModFoliagePlacerTypes;

public class RosewillowFoliagePlacer extends FoliagePlacer {
    public static final Codec<RosewillowFoliagePlacer> CODEC = RecordCodecBuilder
            .create(instance -> fillFoliagePlacerFields(instance)
                    .and(Codec.intRange(0, 16).fieldOf("height").forGetter(placer -> placer.height))
                    .apply(instance, RosewillowFoliagePlacer::new));

    private final int height;

    public RosewillowFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return ModFoliagePlacerTypes.ROSEWILLOW_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config,
            int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        // Generate a large, somewhat spherical canopy
        // Center of the foliage sphere
        BlockPos center = treeNode.getCenter();

        // Vary the radius slightly
        int currentRadius = radius + random.nextInt(2);

        // Determine the vertical range of the foliage
        // foliageHeight is usually passed from the config
        int startY = offset; // Offset from trunk top

        for (int y = startY; y >= -foliageHeight - 2; --y) {
            // Calculate radius at this height for spherical shape
            // (x^2 + z^2 + y^2 = r^2) -> r_at_y = sqrt(r^2 - y^2)
            // We use a modified formula for a slightly flattened or organic shape

            // Percentage of height from center (0 = center, 1 = edge)
            // Let's approximate a sphere centered at trunk top
            int yRel = y;

            // Adjust radius based on y distance from "center" of foliage
            // Let's assume foliage center is slightly below trunk top
            int yDist = Math.abs(yRel);

            int layerRadius = currentRadius;

            if (yDist >= currentRadius) {
                layerRadius = currentRadius - 2;
            } else if (yDist >= currentRadius - 1) {
                layerRadius = currentRadius - 1;
            }

            // Base should be round
            if (layerRadius < 0)
                continue;

            this.generateSquare(world, placer, random, config, center, layerRadius, y, treeNode.isGiantTrunk());
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int dy, int dz, int radius, boolean giantTrunk) {
        // Round off corners
        return dx * dx + dz * dz > radius * radius;
    }
}
