package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.enchantments.ModEnchantments;

public class ExpBoostHandler {

    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((ServerWorld world, Entity user, LivingEntity killedEntity) -> {
            if (!(user instanceof PlayerEntity player)) return;

            ItemStack weapon = player.getMainHandStack();
            int level = EnchantmentHelper.getLevel(ModEnchantments.EXP_BOOST, weapon);
            if (level <= 0) return;

            // Base XP drop
            int baseXp = killedEntity.getXpToDrop();

            // Scale (Lv1 = 2x, Lv5 = 6x = +500%)
            float multiplier = 1.0f + (level * 1.0f);
            int totalXp = Math.round(baseXp * multiplier);

            if (totalXp <= 0) return;

            // Spawn XP orbs
            Vec3d pos = killedEntity.getPos();
            ExperienceOrbEntity.spawn(world, pos, totalXp);
        });
    }
}
