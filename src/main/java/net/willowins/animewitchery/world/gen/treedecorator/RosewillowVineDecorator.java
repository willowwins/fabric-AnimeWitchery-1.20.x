package net.willowins.animewitchery.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.world.gen.ModTreeDecoratorTypes;

import java.util.List;

public class RosewillowVineDecorator extends TreeDecorator {
    public static final Codec<RosewillowVineDecorator> CODEC = Codec.unit(() -> RosewillowVineDecorator.INSTANCE);
    public static final RosewillowVineDecorator INSTANCE = new RosewillowVineDecorator();

    @Override
    protected TreeDecoratorType<?> getType() {
        return ModTreeDecoratorTypes.ROSEWILLOW_VINE_DECORATOR;
    }

    @Override
    public void generate(TreeDecorator.Generator generator) {
        Random random = generator.getRandom();
        List<BlockPos> leavesPositions = generator.getLeavesPositions();

        // Iterate through leaf positions
        for (BlockPos leafPos : leavesPositions) {
            // Check if bottom of leaf cluster (air below)
            if (generator.isAir(leafPos.down())) {
                // 25% chance to spawn a vine column
                if (random.nextInt(4) == 0) {
                    BlockPos currentPos = leafPos.down();
                    // Calculate a random length between 2 and 8 blocks
                    int length = 2 + random.nextInt(7);

                    // Generate vine column
                    for (int i = 0; i < length; i++) {
                        // Stop if we hit something that isn't air
                        if (!generator.isAir(currentPos)) {
                            break;
                        }

                        // Check distance to ground
                        // We want the tip to be at least 2 blocks above ground
                        // This means if we place a block here, and the block below is solid, we stop?
                        // "never less than two blocks above the ground"
                        // This implies we need to check down 2 blocks.
                        // If currentPos.down().down() is NOT air/void, maybe we should stop?
                        // Or simply check if we are too close.
                        // Let's just place down until we hit ground or max length.
                        // But we want to STOP 2 blocks above ground.

                        // Check if block 2 blocks below is solid (roughly)
                        // generator.isAir checks air. If not air, it's likely solid/something.
                        if (!generator.isAir(currentPos.down()) || !generator.isAir(currentPos.down(2))) {
                            // If we are too close to ground, stop here and place the blossom if possible
                            // If we are placing the blossom, we need to be careful.
                            // The request says "never less than two blocks above the ground".
                            // So if we place a blossom at currentPos, currentPos.down() must be air, and
                            // currentPos.down(2) can be ground.
                            // So we need currentPos.down() to be air.
                            break;
                        }

                        // Determine if this is the last block of the vine (tip)
                        boolean isTip = (i == length - 1) || (!generator.isAir(currentPos.down(3))); // Look ahead for
                                                                                                     // ground

                        if (isTip) {
                            // Place Large Rosewillow Blossom at the tip
                            generator.replace(currentPos, ModBlocks.LARGE_ROSEWILLOW_BLOSSOM.getDefaultState());
                            break; // End of vine
                        } else {
                            // Place Rosewillow Vine Body
                            // We should probably randomize berries?
                            BlockState vineState = ModBlocks.ROSEWILLOW_VINES.getDefaultState().with(Properties.BERRIES,
                                    random.nextBoolean());
                            generator.replace(currentPos, vineState);
                        }

                        currentPos = currentPos.down();
                    }
                }
            }
        }
    }
}
