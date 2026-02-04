package net.willowins.animewitchery.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.willowins.animewitchery.entity.ISummonedEntity;

import java.util.EnumSet;

public class FollowSummonerGoal extends Goal {
    private final MobEntity mob;
    private final ISummonedEntity summonedEntity;
    private LivingEntity summoner;
    private final double speed;
    private final float minDistance;
    private final float maxDistance;
    private int updateCountdownTicks;

    public FollowSummonerGoal(MobEntity mob, double speed, float minDistance, float maxDistance) {
        this.mob = mob;
        if (!(mob instanceof ISummonedEntity)) {
            throw new IllegalArgumentException("Mob must implement ISummonedEntity");
        }
        this.summonedEntity = (ISummonedEntity) mob;
        this.speed = speed;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity owner = this.summonedEntity.getSummoner();
        if (owner == null) {
            return false;
        } else if (owner.isSpectator()) {
            return false;
        } else if (this.mob.squaredDistanceTo(owner) < (double) (this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.summoner = owner;
            return true;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.mob.getNavigation().isIdle()) {
            return false;
        } else if (this.mob.squaredDistanceTo(this.summoner) > (double) (this.maxDistance * this.maxDistance)) {
            return false;
        } else {
            return !(this.mob.squaredDistanceTo(this.summoner) <= (double) (this.minDistance * this.minDistance));
        }
    }

    @Override
    public void start() {
        this.updateCountdownTicks = 0;
    }

    @Override
    public void stop() {
        this.summoner = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().lookAt(this.summoner, 10.0F, (float) this.mob.getMaxLookPitchChange());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (this.mob instanceof PathAwareEntity pathAware) {
                pathAware.getNavigation().startMovingTo(this.summoner, this.speed);
            } else {
                this.mob.getNavigation().startMovingTo(this.summoner, this.speed);
            }
        }
    }
}
