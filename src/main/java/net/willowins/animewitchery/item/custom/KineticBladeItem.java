package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class KineticBladeItem extends Item {

    // Base melee damage modifier
    private static final float BASE_DAMAGE = 7.0f;

    // How strongly velocity contributes to bonus damage
    private static final float VELOCITY_MULTIPLIER = 10.0f;

    // Minimum speed required to gain any bonus damage
    private static final double MIN_SPEED = 0.25;

    public KineticBladeItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof PlayerEntity player)) return false;

        World world = player.getWorld();
        Vec3d velocity = player.getVelocity();
        double speed = velocity.length();

        // Calculate velocity-based damage
        float extraDamage = 0;
        if (speed > MIN_SPEED) {
            extraDamage = (float) (speed * VELOCITY_MULTIPLIER);
        }

        // Total damage dealt
        float totalDamage = BASE_DAMAGE + extraDamage;
        target.damage(world.getDamageSources().playerAttack(player), totalDamage);

        // Feedback visuals
        if (!world.isClient) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 1.2f);

            // Small kinetic pulse at high speeds
            if (speed > 1.0) {
                world.createExplosion(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        0.0f, // purely visual
                        false,
                        World.ExplosionSourceType.NONE
                );
            }
        }

        // Small durability drain for high-velocity strikes
        if (speed > 0.8) {
            stack.damage(2, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        } else {
            stack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
        }

        return true;
    }


}
