package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.willowins.animewitchery.fluid.ModFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.willowins.animewitchery.block.ModBlocks;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"))
    private void convertToShimmering(BlockState state, ServerWorld world, BlockPos pos,
            net.minecraft.util.math.random.Random random, CallbackInfo ci) {
        if (state.get(FarmlandBlock.MOISTURE) < 7) {
            // If not fully wet, check specifically for Starlight to convert/hydrate
            // Actually, standard hydration handles moisture.
            // We want conversion.
            // Let's just check for starlight nearby.
            // Accessing isWaterNearby is static... we can replicate the check or assume if
            // it becomes wet it might be starlight.
            // But simpler: Check if Starlight matches the 'water' proximity.
        }

        // Scan for Starlight
        boolean hasStarlight = false;
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getFluidState(blockPos).getFluid() == ModFluids.STILL_STARLIGHT
                    || world.getFluidState(blockPos).getFluid() == ModFluids.FLOWING_STARLIGHT) {
                hasStarlight = true;
                break;
            }
        }

        if (hasStarlight) {
            world.setBlockState(pos, ModBlocks.SHIMMERING_FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7));
            ci.cancel();
        }
    }
}
