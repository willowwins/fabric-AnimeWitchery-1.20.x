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
    private static final int MANA_COST_PER_HIT = 500; // Mana consumed per powered hit
    private static final float DAMAGE_MULTIPLIER = 0.05f; // Bonus damage = total_mana * multiplier
    private static final float MAX_BONUS_DAMAGE = 20.0f; // Cap bonus damage

    public ResonantGreatSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient && attacker instanceof PlayerEntity player) {
            int currentMana = net.willowins.animewitchery.mana.ManaHelper.getTotalMana(player);

            if (currentMana >= MANA_COST_PER_HIT) {
                // Calculate bonus damage based on current mana
                float bonusDamage = Math.min(MAX_BONUS_DAMAGE, currentMana * DAMAGE_MULTIPLIER / 10.0f);

                // Consume mana
                if (net.willowins.animewitchery.mana.ManaHelper.consumeCostFromPlayerAndCatalysts(player,
                        MANA_COST_PER_HIT)) {
                    // Deal bonus damage
                    target.damage(target.getWorld().getDamageSources().playerAttack(player), bonusDamage);

                    // Update immediate NBT for bar responsiveness
                    stack.getOrCreateNbt().putInt("current_mana", currentMana - MANA_COST_PER_HIT);

                    // Spawn shockwave particles on hit
                    if (target.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ModParticles.LASER_PARTICLE,
                                target.getX(), target.getY() + target.getHeight() / 2, target.getZ(),
                                5, 0.3, 0.3, 0.3, 0.1);
                    }

                    // Play sound effect
                    target.getWorld().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS,
                            1.0f, 1.2f);
                }
            }
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot,
            boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player && (selected || slot < 9)) {
            // Regularly sync mana to NBT for the durability bar display
            int totalCurrent = net.willowins.animewitchery.mana.ManaHelper.getTotalMana(player);
            int totalMax = net.willowins.animewitchery.mana.ManaHelper.getTotalMaxMana(player);

            stack.getOrCreateNbt().putInt("current_mana", totalCurrent);
            stack.getOrCreateNbt().putInt("max_mana", totalMax);
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        int current = stack.getOrCreateNbt().getInt("current_mana");
        int max = stack.getOrCreateNbt().getInt("max_mana");

        tooltip.add(Text.literal("Mana: " + current + " / " + max).formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Consumes 500 Mana per hit for bonus damage").formatted(Formatting.GRAY));
    }
}
