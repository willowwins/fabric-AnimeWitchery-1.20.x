package net.willowins.animewitchery.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;
import java.util.HashSet;

/**
 * When a lightning rod is struck and Ancient Debris is directly beneath it,
 * the debris "infects" nearby Netherrack blocks (sculk-ish spread),
 * and has a 1% chance to transmute the seed debris into a Netherite Block.
 */
@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin {

    // Tuning knobs (start conservative; Ancient Debris is spicy).
    private static final int SPREAD_RADIUS = 8;              // max Manhattan distance from seed
    private static final int INITIAL_CHARGE = 64;            // BFS budget
    private static final int CHARGE_DECAY_PER_STEP = 4;      // charge loss when branching outward
    private static final int MAX_CONVERSIONS = 32;           // cap per strike
    private static final float BASE_SPREAD_CHANCE = 0.65f;   // scaled by remaining charge
    private static final float SEED_TO_NETHERITE_CHANCE = 0.01f; // 1%

    private static final Direction[] CARDINAL_AND_VERTICAL = new Direction[] {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN
    };
    // In Yarn 1.20.x LightningRodBlock has: onLightningStrike(BlockState, World, BlockPos)
    // Some versions also include LightningEntity; if your method signature differs, adjust accordingly.
    @Inject(method = "setPowered", at = @At("TAIL"))
    private void animewitchery$debrisSpreadOnRodStrike(BlockState state, net.minecraft.world.World world, BlockPos rodPos, CallbackInfo ci) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        BlockPos seedPos = rodPos.down();
        BlockState seedState = serverWorld.getBlockState(seedPos);

        if (!seedState.isOf(Blocks.ANCIENT_DEBRIS)) return;

        // 1% chance: transmute the seed block to Netherite Block.
        // (If you prefer "netherite debris" fantasy, swap this for your own block.)
        if (serverWorld.random.nextFloat() < SEED_TO_NETHERITE_CHANCE) {
            serverWorld.setBlockState(seedPos, Blocks.NETHERITE_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
            // If you want the spread to still happen after transmutation, remove this return.
            // return;
        }

        spreadDebris(serverWorld, seedPos);
    }

    private static void spreadDebris(ServerWorld world, BlockPos seedPos) {
        ArrayDeque<BlockPos> posQueue = new ArrayDeque<>();
        ArrayDeque<Integer> chargeQueue = new ArrayDeque<>();
        HashSet<BlockPos> visited = new HashSet<>();

        posQueue.add(seedPos);
        chargeQueue.add(INITIAL_CHARGE);
        visited.add(seedPos);

        int conversions = 0;

        while (!posQueue.isEmpty() && conversions < MAX_CONVERSIONS) {
            BlockPos pos = posQueue.removeFirst();
            int charge = chargeQueue.removeFirst();

            if (charge <= 0) continue;

            for (Direction dir : CARDINAL_AND_VERTICAL) {
                BlockPos nextPos = pos.offset(dir);

                if (manhattan(seedPos, nextPos) > SPREAD_RADIUS) continue;
                if (!visited.add(nextPos)) continue;

                BlockState nextState = world.getBlockState(nextPos);

                if (nextState.isOf(Blocks.NETHERRACK)) {
                    float scaledChance = BASE_SPREAD_CHANCE * (charge / (float) INITIAL_CHARGE);

                    if (world.random.nextFloat() < scaledChance) {
                        world.setBlockState(nextPos, Blocks.ANCIENT_DEBRIS.getDefaultState(), Block.NOTIFY_ALL);
                        conversions++;

                        posQueue.addLast(nextPos);
                        chargeQueue.addLast(charge - CHARGE_DECAY_PER_STEP);
                    } else {
                        posQueue.addLast(nextPos);
                        chargeQueue.addLast(charge - (CHARGE_DECAY_PER_STEP * 2));
                    }

                } else if (nextState.isOf(Blocks.ANCIENT_DEBRIS)) {
                    posQueue.addLast(nextPos);
                    chargeQueue.addLast(charge - Math.max(1, CHARGE_DECAY_PER_STEP / 2));
                }

                if (conversions >= MAX_CONVERSIONS) break;
            }
        }
    }

    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX())
                + Math.abs(a.getY() - b.getY())
                + Math.abs(a.getZ() - b.getZ());
    }
}