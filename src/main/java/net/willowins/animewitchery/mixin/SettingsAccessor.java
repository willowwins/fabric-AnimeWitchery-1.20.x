package net.willowins.animewitchery.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.Settings.class)
public interface SettingsAccessor {
    @Accessor("hardness")
    float getHardness();

    @Accessor("resistance")
    float getResistance();

    @Accessor("soundGroup")
    BlockSoundGroup getSoundGroup();

    @Accessor("toolRequired")
    boolean isToolRequired();

    @Accessor("opaque")
    boolean getOpaque();
}
