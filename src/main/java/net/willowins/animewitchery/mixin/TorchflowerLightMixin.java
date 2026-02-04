package net.willowins.animewitchery.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class TorchflowerLightMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getLuminance", at = @At("HEAD"), cancellable = true)
    private void getCustomLuminance(CallbackInfoReturnable<Integer> cir) {
        if (this.getBlock() == Blocks.TORCHFLOWER || this.getBlock() == Blocks.POTTED_TORCHFLOWER) {
            cir.setReturnValue(14);
        }
    }
}
