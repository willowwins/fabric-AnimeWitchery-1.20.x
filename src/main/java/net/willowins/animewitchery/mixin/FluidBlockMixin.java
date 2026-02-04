package net.willowins.animewitchery.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    @Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
    private void animeWitchery$customOreGen(World world, BlockPos pos, BlockState state,
            CallbackInfoReturnable<Boolean> cir) {
        if (state.isOf(Blocks.LAVA)) {
            boolean touchingWater = false;
            for (Direction direction : Direction.values()) {
                if (direction != Direction.DOWN) {
                    BlockPos neighborPos = pos.offset(direction);
                    if (world.getFluidState(neighborPos).isIn(net.minecraft.registry.tag.FluidTags.WATER)) {
                        touchingWater = true;
                        break;
                    }
                }
            }

            if (touchingWater) {
                BlockState blockBelow = world.getBlockState(pos.down());

                if (blockBelow.isOf(Blocks.SOUL_SAND)) {
                    world.setBlockState(pos, Blocks.TUFF.getDefaultState());
                    world.syncWorldEvent(1501, pos, 0); // Fizz sound
                    cir.setReturnValue(true);
                    return;
                }

                BlockState blockAbove = world.getBlockState(pos.up());

                if (isValidOreGenerator(blockAbove, blockBelow)) {
                    world.setBlockState(pos, blockAbove);
                    world.syncWorldEvent(1501, pos, 0); // Fizz sound
                    cir.setReturnValue(true);
                }
            }
        }
    }

    private boolean isValidOreGenerator(BlockState oreAbove, BlockState materialBelow) {
        Block ore = oreAbove.getBlock();
        Block material = materialBelow.getBlock();

        // Stone Ores
        if (material == Blocks.STONE) {
            return ore == Blocks.COAL_ORE
                    || ore == Blocks.IRON_ORE
                    || ore == Blocks.COPPER_ORE
                    || ore == Blocks.GOLD_ORE
                    || ore == Blocks.REDSTONE_ORE
                    || ore == Blocks.LAPIS_ORE
                    || ore == Blocks.DIAMOND_ORE
                    || ore == Blocks.EMERALD_ORE
                    || ore == net.willowins.animewitchery.block.ModBlocks.SILVER_ORE;
        }

        // Deepslate Ores
        if (material == Blocks.DEEPSLATE) {
            return ore == Blocks.DEEPSLATE_COAL_ORE
                    || ore == Blocks.DEEPSLATE_IRON_ORE
                    || ore == Blocks.DEEPSLATE_COPPER_ORE
                    || ore == Blocks.DEEPSLATE_GOLD_ORE
                    || ore == Blocks.DEEPSLATE_REDSTONE_ORE
                    || ore == Blocks.DEEPSLATE_LAPIS_ORE
                    || ore == Blocks.DEEPSLATE_DIAMOND_ORE
                    || ore == Blocks.DEEPSLATE_EMERALD_ORE
                    || ore == net.willowins.animewitchery.block.ModBlocks.DEEPSLATE_SILVER_ORE;
        }

        // Nether Ores
        if (material == Blocks.NETHERRACK) {
            return ore == Blocks.NETHER_QUARTZ_ORE
                    || ore == Blocks.NETHER_GOLD_ORE;
        }

        return false;
    }
}
