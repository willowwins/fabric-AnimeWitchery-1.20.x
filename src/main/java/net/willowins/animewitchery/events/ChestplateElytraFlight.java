package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents; // If your API names it LivingEntityElytraEvents, just swap the import & type.
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.willowins.animewitchery.enchantments.ModEnchantments;

public final class ChestplateElytraFlight {
    private ChestplateElytraFlight() {}

    /** Call once during common setup (e.g., in your ModInitializer#onInitialize). */
    public static void register() {
        EntityElytraEvents.CUSTOM.register((entity, tickElytra) -> {
            if (!(entity instanceof PlayerEntity player)) return false;

            ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
            if (!(chest.getItem() instanceof ArmorItem)) return false;

            int level = EnchantmentHelper.getLevel(ModEnchantments.FLIGHT_ENCHANT, chest);
            if (level <= 0) return false;

            if (chest.isDamageable() && chest.getMaxDamage() - chest.getDamage() <= 1) return false;

            // Side-effects (server only) while flying:
            if (tickElytra && !player.getWorld().isClient()) {
                // Vanilla Elytra damages every 20 ticks; mirror that cadence.
                if (player.age % Math.max(20 - (level - 1) * 5, 5) == 0) { // higher level = slightly cheaper upkeep
                    chest.damage(1, player, p -> p.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
                }


            }

            // Returning true enables/continues elytra flight for this tick.
            return true;
        });
    }
}
