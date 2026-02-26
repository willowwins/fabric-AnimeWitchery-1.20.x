package net.willowins.animewitchery.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ai.FollowCrownGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemFollowMixin extends GolemEntity {

    protected IronGolemFollowMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addKnightFollowGoal(CallbackInfo ci) {
        this.goalSelector.add(6, new FollowCrownGoal(this, 1.0, 10.0F));
    }
}
