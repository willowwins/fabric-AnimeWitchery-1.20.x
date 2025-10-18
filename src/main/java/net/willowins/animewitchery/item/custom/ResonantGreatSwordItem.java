package net.willowins.animewitchery.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.willowins.animewitchery.particle.ModParticles;

import java.util.List;

public class ResonantGreatSwordItem extends SwordItem {
    private static final int MAX_USE_TIME = 72000; // Can block indefinitely
    private static final float BLOCK_DAMAGE_REDUCTION = 0.5f; // 50% damage reduction when blocking
    private static final float CHARGE_GAIN_PER_DAMAGE = 0.1f; // Gain 0.1 charge per damage point blocked
    private static final float CHARGE_LOSS_PER_ATTACK = 2.0f; // Lose 2 charge per attack (regardless of damage)
    private static final float MAX_CHARGE = 20.0f; // Max charge value
    private static final float DAMAGE_MULTIPLIER = 2.0f; // Bonus damage = charge * multiplier

    public ResonantGreatSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TIME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        
        // Always start blocking/using
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Get current charge
        float charge = stack.getOrCreateNbt().getFloat("charge");
        
        if (charge > 0 && attacker instanceof PlayerEntity player) {
            // Calculate bonus damage based on current charge (charge * multiplier)
            float bonusDamage = charge * DAMAGE_MULTIPLIER;
            
            // Deal bonus damage
            target.damage(target.getWorld().getDamageSources().playerAttack(player), bonusDamage);
            
            // Reduce charge by a fixed amount per attack
            float newCharge = Math.max(0, charge - CHARGE_LOSS_PER_ATTACK);
            stack.getOrCreateNbt().putFloat("charge", newCharge);
            
            // Spawn shockwave particles on hit (more particles for higher charge)
            if (target.getWorld() instanceof ServerWorld serverWorld) {
                int particleCount = Math.max(3, Math.round(charge / 4));
                serverWorld.spawnParticles(ModParticles.LASER_PARTICLE,
                    target.getX(), target.getY() + target.getHeight() / 2, target.getZ(),
                    particleCount, 0.3, 0.3, 0.3, 0.1);
            }
            
            // Play sound effect (pitch based on charge)
            float pitch = 0.8f + (charge / MAX_CHARGE) * 0.4f;
            target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS,
                1.0f, pitch);
        }
        
        return super.postHit(stack, target, attacker);
    }

    // This method will be called by a mixin when the player blocks damage
    public static void addChargeFromBlockedDamage(ItemStack stack, float damageBlocked) {
        float currentCharge = stack.getOrCreateNbt().getFloat("charge");
        float newCharge = Math.min(MAX_CHARGE, currentCharge + (damageBlocked * CHARGE_GAIN_PER_DAMAGE));
        stack.getOrCreateNbt().putFloat("charge", newCharge);
    }

    public static float getBlockDamageReduction() {
        return BLOCK_DAMAGE_REDUCTION;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        // Always show the bar
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        // Convert charge (0-100) to bar steps (0-13)
        float charge = stack.getOrCreateNbt().getFloat("charge");
        return Math.round((charge / MAX_CHARGE) * 13.0f);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        // Purple color (similar to enchanted items)
        return 0xAA00FF; // Bright purple
    }

    // Method to get current charge (for mixin access)
    public static float getCharge(ItemStack stack) {
        return stack.getOrCreateNbt().getFloat("charge");
    }

    public static float getMaxCharge() {
        return MAX_CHARGE;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        
        float charge = stack.getOrCreateNbt().getFloat("charge");
        float chargePercent = (charge / MAX_CHARGE) * 100.0f;
        int displayPercent = Math.round(chargePercent);
        
        // Color the charge text based on the charge level
        Formatting color;
        if (chargePercent >= 100) {
            color = Formatting.LIGHT_PURPLE; // Full charge - bright purple
        } else if (chargePercent >= 75) {
            color = Formatting.DARK_PURPLE; // High charge - dark purple
        } else if (chargePercent >= 50) {
            color = Formatting.BLUE; // Medium charge - blue
        } else if (chargePercent >= 25) {
            color = Formatting.DARK_BLUE; // Low charge - dark blue
        } else {
            color = Formatting.GRAY; // Very low charge - gray
        }
        
        tooltip.add(Text.literal("Charge: " + displayPercent + "%").formatted(color));
        
        if (charge > 0) {
            float bonusDamage = charge * DAMAGE_MULTIPLIER;
            tooltip.add(Text.literal("Bonus Damage: +" + String.format("%.1f", bonusDamage)).formatted(Formatting.RED));
        }
    }
}
