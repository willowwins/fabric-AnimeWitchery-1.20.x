package net.willowins.animewitchery.enchantments.custom;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LeadCoatedEnchantment extends Enchantment {
    // base percentage bonus per block/sec of velocity
    private static final float VELOCITY_MULTIPLIER = 2f;

    // minimum movement speed required before scaling applies
    private static final double SPEED_THRESHOLD = 0.25;

    public LeadCoatedEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if (!(user instanceof PlayerEntity player)) return;
        World world = player.getWorld();

        // measure current velocity magnitude
        Vec3d velocity = player.getVelocity();
        double speed = velocity.length();

        // only enhance if moving fast enough
        if (speed < SPEED_THRESHOLD) return;

        // damage multiplier: increases with both speed and enchant level
        float multiplier = 1.0f + (float) (speed * (VELOCITY_MULTIPLIER * level));

        float baseDamage = 4.0f; // nominal addend for tuning
        float extraDamage = baseDamage * (multiplier - 1.0f);

        target.damage(world.getDamageSources().playerAttack(player), extraDamage);

        // light knockback proportional to momentum
        Vec3d push = target.getPos().subtract(player.getPos()).normalize().multiply(speed * 0.8);
        target.setVelocity(push.x, 0.3, push.z);
        target.velocityModified = true;

        super.onTargetDamaged(user, target, level);
    }
}
