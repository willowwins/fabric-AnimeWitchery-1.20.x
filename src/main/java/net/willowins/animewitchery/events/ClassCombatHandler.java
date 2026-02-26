package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.willowins.animewitchery.item.custom.AssassinDaggerItem;

public class ClassCombatHandler {

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient)
                return ActionResult.PASS;

            ItemStack mainHand = player.getMainHandStack();

            // --- Assassin Logic ---
            if (mainHand.getItem() instanceof AssassinDaggerItem && entity instanceof LivingEntity target) {
                boolean isBehind = AssassinDaggerItem.isBehind(player, target);
                boolean isInvisible = player.hasStatusEffect(StatusEffects.INVISIBILITY);

                if (isBehind || isInvisible) {
                    float baseDamage = (float) player
                            .getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    float bonus = baseDamage * 0.5f;

                    if (player.getAttackCooldownProgress(0.5f) >= 0.9f) {
                        target.damage(player.getDamageSources().playerAttack(player), bonus);

                        // Reveal Assassin
                        player.removeStatusEffect(StatusEffects.INVISIBILITY);
                        player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.willowins.animewitchery.effect.ModEffects.REVEALED, 300, 0, false, false, true));
                    }
                }
            }

            // --- Sanguine Logic ---
            if (mainHand.getItem() instanceof net.willowins.animewitchery.item.custom.BloodletterItem) {
                float healAmount = 1.0f; // 0.5 Hearts

                // Hooded Cape Buff
                if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST)
                        .getItem() instanceof net.willowins.animewitchery.item.custom.HoodedCapeItem) {
                    healAmount = 2.0f; // 1 Heart
                }

                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(healAmount);
                } else {
                    player.getHungerManager().add(1, 0.5f);
                }
            }

            // --- Femboy Logic ---
            if (mainHand.getItem() instanceof net.willowins.animewitchery.item.custom.StuffedAnimalItem) {
                return ActionResult.FAIL; // Cancel attack entirely ("Unable to do damage")
            }

            return ActionResult.PASS;
        });
    }
}
