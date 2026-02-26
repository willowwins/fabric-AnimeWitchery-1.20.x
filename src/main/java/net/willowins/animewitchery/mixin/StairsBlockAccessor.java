package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StairsBlock.class)
public interface StairsBlockAccessor {
    @Accessor("baseBlockState")
    BlockState getBaseBlockState();
}
