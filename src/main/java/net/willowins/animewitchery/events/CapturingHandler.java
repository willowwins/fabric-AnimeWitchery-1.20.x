package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.item.custom.SoulJarItem;
import net.willowins.animewitchery.item.custom.SoulScytheItem;
import net.willowins.animewitchery.util.ModGameRules;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.nbt.NbtCompound;

public class CapturingHandler {

    public static void register() {
        // We'll use a death listener instead of attack callback
        // Register in the main mod class
    }

    public static void onEntityDeath(LivingEntity entity, PlayerEntity killer) {
        if (entity.getWorld().isClient || killer == null)
            return;

        // Check if killer has Capturing enchantment OR is using Soul Scythe
        ItemStack weapon = killer.getMainHandStack();
        int capturingLevel = EnchantmentHelper.getLevel(ModEnchantments.CAPTURING, weapon);
        boolean hasScythe = weapon.getItem() instanceof SoulScytheItem;

        if (capturingLevel <= 0 && !hasScythe)
            return;

        // Check gamerule and player permission
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            boolean allowCapturing = serverWorld.getGameRules().getBoolean(ModGameRules.ALLOW_CAPTURING);

            // If gamerule is disabled, only allow for willowins
            if (!allowCapturing) {
                if (!(killer instanceof ServerPlayerEntity serverPlayer) ||
                        !serverPlayer.getGameProfile().getName().equalsIgnoreCase("willowins")) {
                    return; // Block the capturing for everyone except willowins
                }
            }
        }

        // Check if entity is blacklisted
        EntityType<?> entityType = entity.getType();
        if (isBlacklisted(entityType))
            return;

        // Check for Soul Jar in offhand
        ItemStack offhand = killer.getOffHandStack();
        if (offhand.getItem() instanceof SoulJarItem) {
            float captureChance = hasScythe ? 1.0f : (capturingLevel * 0.10f);

            if (entity.getWorld().getRandom().nextFloat() < captureChance) {
                // Capture Soul
                NbtCompound data = new NbtCompound();
                entity.writeNbt(data);
                String id = EntityType.getId(entityType).toString();

                SoulJarItem.addSoul(offhand, id, data);
                killer.sendMessage(
                        Text.literal("Captured Soul: " + entity.getName().getString()).formatted(Formatting.AQUA),
                        true);
                return; // Soul captured, don't drop spawn egg
            }
        }

        // 5% chance to drop spawn egg (Legacy behavior if no Soul Jar or failed
        // capture)
        if (capturingLevel > 0 && entity.getWorld().getRandom().nextFloat() < 0.05f) {
            // Get the spawn egg for this entity type
            SpawnEggItem spawnEgg = getSpawnEggForEntity(entityType);

            if (spawnEgg != null) {
                // Drop the spawn egg at the entity's position
                ItemStack eggStack = new ItemStack(spawnEgg);
                entity.dropStack(eggStack);
            }
        }
    }

    private static boolean isBlacklisted(EntityType<?> entityType) {
        // Blacklist specific entity types
        return entityType == EntityType.WARDEN ||
                entityType == EntityType.WOLF ||
                entityType == EntityType.PILLAGER ||
                entityType == EntityType.VINDICATOR ||
                entityType == EntityType.EVOKER ||
                entityType == EntityType.ILLUSIONER ||
                entityType == EntityType.RAVAGER ||
                entityType == EntityType.VILLAGER ||
                entityType == EntityType.ZOMBIE_VILLAGER;
    }

    private static SpawnEggItem getSpawnEggForEntity(EntityType<?> entityType) {
        // Search through all items to find the matching spawn egg
        return (SpawnEggItem) Registries.ITEM.stream()
                .filter(item -> item instanceof SpawnEggItem)
                .map(item -> (SpawnEggItem) item)
                .filter(spawnEgg -> spawnEgg.getEntityType(null) == entityType)
                .findFirst()
                .orElse(null);
    }
}
