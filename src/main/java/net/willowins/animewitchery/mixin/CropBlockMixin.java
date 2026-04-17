package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.AttachedStemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.willowins.animewitchery.block.ModBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CropBlock.class, StemBlock.class, AttachedStemBlock.class})
public class CropBlockMixin {
    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    protected void allowPlantingOnShimmeringFarmland(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (floor.isOf(ModBlocks.SHIMMERING_FARMLAND)) {
            cir.setReturnValue(true);
        }
    }
}
