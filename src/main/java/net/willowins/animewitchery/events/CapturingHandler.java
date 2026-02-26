package net.willowins.animewitchery.events;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.item.custom.SoulScytheItem;
import net.willowins.animewitchery.util.ModGameRules;
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

        if (!hasScythe)
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

            boolean isTamedWolf = entity instanceof net.minecraft.entity.passive.WolfEntity wolf && wolf.isTamed();

            // Check if entity is blacklisted (bypass for tamed wolves)
            EntityType<?> entityType = entity.getType();
            if (isBlacklisted(entityType) && !isTamedWolf)
                return;

            // Special handling for Tamed Wolves (always capture, give to owner)
            if (isTamedWolf) {
                net.minecraft.entity.passive.WolfEntity wolf = (net.minecraft.entity.passive.WolfEntity) entity;

                // Create Soul Item
                NbtCompound entityData = new NbtCompound();
                entity.saveNbt(entityData);

                ItemStack soulStack = new ItemStack(net.willowins.animewitchery.item.ModItems.SOUL);
                NbtCompound soulNbt = soulStack.getOrCreateNbt();
                soulNbt.put("EntityData", entityData);
                if (entity.hasCustomName()) {
                    soulNbt.putString("EntityName", entity.getCustomName().getString());
                } else {
                    soulNbt.putString("EntityName", entity.getType().getName().getString());
                }

                // Find Owner
                java.util.UUID ownerUuid = wolf.getOwnerUuid();
                if (ownerUuid != null) {
                    ServerPlayerEntity owner = serverWorld.getServer().getPlayerManager().getPlayer(ownerUuid);
                    if (owner != null) {
                        // Give to owner
                        if (!owner.getInventory().insertStack(soulStack)) {
                            // Inventory full, drop at owner
                            owner.dropItem(soulStack, false);
                            owner.sendMessage(net.minecraft.text.Text
                                    .literal("Your pet's soul was dropped nearby (Inventory Full).")
                                    .formatted(net.minecraft.util.Formatting.RED), false);
                        } else {
                            owner.sendMessage(net.minecraft.text.Text.literal("Your pet's soul has returned to you.")
                                    .formatted(net.minecraft.util.Formatting.GREEN), true);
                        }
                    } else {
                        // Owner offline, store in persistent state
                        net.willowins.animewitchery.world.SoulRecoveryState state = net.willowins.animewitchery.world.SoulRecoveryState
                                .getServerState(serverWorld);
                        if (state != null) {
                            state.addSoul(ownerUuid, soulStack);
                            // Optional: Is there a way to notify them next time? The join event will handle
                            // it.
                        } else {
                            // Fallback if state fails (unlikely)
                            entity.dropStack(soulStack);
                        }
                    }
                } else {
                    entity.dropStack(soulStack);
                }
                return; // Done for wolf
            }

            if (capturingLevel > 0 || hasScythe) {
                // Calculate chance
                double chance = 0.05 + (capturingLevel * 0.05);
                if (hasScythe)
                    chance += 0.20;

                if (Math.random() < chance) {
                    // Capture!
                    NbtCompound entityData = new NbtCompound();
                    entity.saveNbt(entityData);

                    // Create Soul Item
                    ItemStack soulStack = new ItemStack(net.willowins.animewitchery.item.ModItems.SOUL);
                    NbtCompound soulNbt = soulStack.getOrCreateNbt();
                    soulNbt.put("EntityData", entityData);
                    if (entity.hasCustomName()) {
                        soulNbt.putString("EntityName", entity.getCustomName().getString());
                    } else {
                        soulNbt.putString("EntityName", entity.getType().getName().getString());
                    }

                    // Try to put in Soul Jar
                    boolean stored = false;
                    ItemStack offhand = killer.getOffHandStack();
                    if (offhand.getItem() instanceof net.willowins.animewitchery.item.custom.SoulJarItem) {
                        stored = storeInJar(offhand, soulStack);
                    } else {
                        // Drop if no jar
                    }

                    if (stored) {
                        killer.sendMessage(net.minecraft.text.Text.literal("Captured Soul!")
                                .formatted(net.minecraft.util.Formatting.GREEN), true);
                    } else {
                        // Drop soul item if no jar or full
                        entity.dropStack(soulStack);
                    }
                }
            }
        }
    }

    // Helper to store item in jar
    private static boolean storeInJar(ItemStack jar, ItemStack soul) {
        net.minecraft.inventory.SimpleInventory inv = new net.minecraft.inventory.SimpleInventory(27);
        net.willowins.animewitchery.item.custom.SoulJarItem.loadInventory(jar, inv);

        // Try to add
        boolean added = false;
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, soul.copy());
                added = true;
                break;
            }
        }

        if (added) {
            net.willowins.animewitchery.item.custom.SoulJarItem.saveInventory(jar, inv);
        }
        return added;
    }

    private static boolean isBlacklisted(EntityType<?> entityType) {
        // Blacklist specific entity types
        return entityType == EntityType.WARDEN ||
                entityType == EntityType.WITHER ||
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
