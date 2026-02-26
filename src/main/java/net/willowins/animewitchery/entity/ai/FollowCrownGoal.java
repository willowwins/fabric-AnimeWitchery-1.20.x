package net.willowins.animewitchery.entity.ai;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.ModItems;

import java.util.EnumSet;

public class FollowCrownGoal extends Goal {
    private final MobEntity mob;
    private PlayerEntity targetPlayer;
    private final double speed;
    private final float stopDistance;

    public FollowCrownGoal(MobEntity mob, double speed, float stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.stopDistance = stopDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // Find nearest player wearing the Crown
        this.targetPlayer = this.mob.getWorld().getClosestPlayer(this.mob, 20.0);
        return this.targetPlayer != null && isWearingCrown(this.targetPlayer);
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPlayer != null &&
                this.targetPlayer.isAlive() &&
                isWearingCrown(this.targetPlayer) &&
                this.mob.squaredDistanceTo(this.targetPlayer) > (double) (this.stopDistance * this.stopDistance);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetPlayer != null) {
            this.mob.getLookControl().lookAt(this.targetPlayer, 10.0F, (float) this.mob.getMaxLookPitchChange());
            if (this.mob.squaredDistanceTo(this.targetPlayer) > (double) (this.stopDistance * this.stopDistance)) {
                this.mob.getNavigation().startMovingTo(this.targetPlayer, this.speed);
            }
        }
    }

    private boolean isWearingCrown(PlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(EquipmentSlot.HEAD);
        return helmet.getItem() == ModItems.CROWN;
    }
}
