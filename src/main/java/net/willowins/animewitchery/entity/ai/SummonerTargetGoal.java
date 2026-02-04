package net.willowins.animewitchery.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.willowins.animewitchery.entity.ISummonedEntity;

import java.util.EnumSet;

public class SummonerTargetGoal extends Goal {
    private final MobEntity mob;
    private final ISummonedEntity summonedEntity;
    private LivingEntity target;
    private int timestamp;

    public SummonerTargetGoal(MobEntity mob) {
        this.mob = mob;
        if (!(mob instanceof ISummonedEntity)) {
            throw new IllegalArgumentException("Mob must implement ISummonedEntity");
        }
        this.summonedEntity = (ISummonedEntity) mob;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        LivingEntity owner = this.summonedEntity.getSummoner();
        if (owner == null) {
            return false;
        } else {
            this.target = owner.getAttacker();
            // Prioritize defending the owner
            if (this.target != null && this.canTrack(this.target, TargetPredicate.DEFAULT)) {
                return true;
            }

            // Then attack owner's target
            this.target = owner.getAttacking();
            if (this.target != null && this.canTrack(this.target, TargetPredicate.DEFAULT)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        LivingEntity owner = this.summonedEntity.getSummoner();
        if (owner != null) {
            this.timestamp = owner.getLastAttackedTime();
        }
        super.start();
    }

    private boolean canTrack(LivingEntity target, TargetPredicate predicate) {
        if (target == null)
            return false;
        if (target == this.summonedEntity.getSummoner())
            return false; // Don't attack owner
        return this.mob.canTarget(target);
    }
}
