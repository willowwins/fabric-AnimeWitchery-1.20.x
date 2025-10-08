package net.willowins.animewitchery.mixin.client;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityRiptideAccessor {
    @Accessor("riptideTicks")
    void setRiptideTicks(int value);

    @Invoker("setLivingFlag")
    void invokeSetLivingFlag(int mask, boolean value);
}
