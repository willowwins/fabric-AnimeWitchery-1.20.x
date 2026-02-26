package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.lodestone.*;

public class ModPackets {
    // Lodestone Packets
    public static final Identifier LASER_BEAM = new Identifier(AnimeWitchery.MOD_ID, "laser_beam");
    public static final Identifier LASER_CHARGE = new Identifier(AnimeWitchery.MOD_ID, "laser_charge");
    public static final Identifier LASER_HIT = new Identifier(AnimeWitchery.MOD_ID, "laser_hit");
    public static final Identifier OBELISK_SHAKE = new Identifier(AnimeWitchery.MOD_ID, "obelisk_shake");
    public static final Identifier RAILGUN_BEAM = new Identifier(AnimeWitchery.MOD_ID, "railgun_beam");
    public static final Identifier RESONANT_BEAM_PARTICLE = new Identifier(AnimeWitchery.MOD_ID,
            "resonant_beam_particle");
    public static final Identifier KAMIKAZE_FX = new Identifier(AnimeWitchery.MOD_ID, "kamikaze_fx");
    public static final Identifier CREATE_PLATFORM_ID = new Identifier("animewitchery", "create_platform");
    public static final Identifier REMOVE_SOUL = new Identifier(AnimeWitchery.MOD_ID, "remove_soul");
    public static final Identifier SWAP_SOULS = new Identifier(AnimeWitchery.MOD_ID, "swap_souls");
    public static final Identifier SELECT_CLASS_ID = new Identifier(AnimeWitchery.MOD_ID, "select_class");
    public static final Identifier UNLOCK_SKILL_ID = new Identifier(AnimeWitchery.MOD_ID, "unlock_skill");
    public static final Identifier SYNC_ENCHANTMENT_LISTS = new Identifier(AnimeWitchery.MOD_ID,
            "sync_enchantment_lists");

    public static void registerC2SPackets() {
        // Client-to-Server packets (registered on the server)
        ServerPlayNetworking.registerGlobalReceiver(CREATE_PLATFORM_ID,
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        if (player != null) {
                            net.willowins.animewitchery.events.PlatformCreator.tryCreatePlatform(player);
                        }
                    });
                });

        ServerPlayNetworking.registerGlobalReceiver(SELECT_CLASS_ID, (server, player, handler, buf, responseSender) -> {
            String className = buf.readString();
            server.execute(() -> {
                net.willowins.animewitchery.component.IClassComponent classData = net.willowins.animewitchery.mana.ModComponents.CLASS_DATA
                        .get(player);

                if (net.willowins.animewitchery.component.ClassRegistry.isValidClass(className)) {
                    // Set Primary
                    if (classData.getPrimaryClass().isEmpty()) {
                        classData.setPrimaryClass(className);
                    }
                    // Set Secondary (Multiclass)
                    else if (classData.getLevel() >= 100 && !classData.hasSecondaryClass()
                            && !classData.getPrimaryClass().equals(className)) {
                        classData.setSecondaryClass(className);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(UNLOCK_SKILL_ID, (server, player, handler, buf, responseSender) -> {
            String skillId = buf.readString();
            server.execute(() -> {
                net.willowins.animewitchery.component.IClassComponent classData = net.willowins.animewitchery.mana.ModComponents.CLASS_DATA
                        .get(player);

                net.willowins.animewitchery.component.SkillRegistry.SkillDef skill = net.willowins.animewitchery.component.SkillRegistry
                        .get(skillId);

                if (skill != null && !classData.isSkillUnlocked(skillId)) {
                    // Check cost
                    if (classData.getSkillPoints() >= skill.cost) {
                        // Check parent (if any)
                        if (skill.parentId == null || classData.isSkillUnlocked(skill.parentId)) {
                            classData.consumeSkillPoints(skill.cost);
                            classData.unlockSkill(skillId);
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(REMOVE_SOUL, (server, player, handler, buf, responseSender) -> {
            int index = buf.readInt();
            server.execute(() -> {
                if (player == null)
                    return;
                net.minecraft.item.ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof net.willowins.animewitchery.item.custom.SoulJarItem)) {
                    stack = player.getOffHandStack();
                }
                if (stack.getItem() instanceof net.willowins.animewitchery.item.custom.SoulJarItem) {
                    net.minecraft.inventory.SimpleInventory inv = new net.minecraft.inventory.SimpleInventory(27);
                    net.willowins.animewitchery.item.custom.SoulJarItem.loadInventory(stack, inv);
                    if (index >= 0 && index < inv.size()) {
                        inv.setStack(index, net.minecraft.item.ItemStack.EMPTY);
                        net.willowins.animewitchery.item.custom.SoulJarItem.saveInventory(stack, inv);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SWAP_SOULS, (server, player, handler, buf, responseSender) -> {
            int idx1 = buf.readInt();
            int idx2 = buf.readInt();
            server.execute(() -> {
                if (player == null)
                    return;
                net.minecraft.item.ItemStack stack = player.getMainHandStack();
                if (!(stack.getItem() instanceof net.willowins.animewitchery.item.custom.SoulJarItem)) {
                    stack = player.getOffHandStack();
                }
                if (stack.getItem() instanceof net.willowins.animewitchery.item.custom.SoulJarItem) {
                    net.minecraft.inventory.SimpleInventory inv = new net.minecraft.inventory.SimpleInventory(27);
                    net.willowins.animewitchery.item.custom.SoulJarItem.loadInventory(stack, inv);

                    if (idx1 >= 0 && idx1 < inv.size() && idx2 >= 0 && idx2 < inv.size()) {
                        net.minecraft.item.ItemStack s1 = inv.getStack(idx1);
                        net.minecraft.item.ItemStack s2 = inv.getStack(idx2);
                        inv.setStack(idx1, s2);
                        inv.setStack(idx2, s1);
                        net.willowins.animewitchery.item.custom.SoulJarItem.saveInventory(stack, inv);
                    }
                }
            });
        });
    }

    public static void registerS2CPackets() {
        // Server-to-Client packets (registered on the client)
        ClientPlayNetworking.registerGlobalReceiver(KAMIKAZE_FX, KamikazeFxPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_BEAM, LaserBeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_CHARGE, LaserChargePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_HIT, LaserHitPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(OBELISK_SHAKE, ObeliskShake::receive);
        ClientPlayNetworking.registerGlobalReceiver(RAILGUN_BEAM, RailgunBeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(RESONANT_BEAM_PARTICLE, ResonantBeamParticlePacket::receive);

        ClientPlayNetworking.registerGlobalReceiver(SYNC_ENCHANTMENT_LISTS, (client, handler, buf, responseSender) -> {
            int syncId = buf.readInt();
            int count = buf.readInt();
            java.util.List<net.minecraft.enchantment.EnchantmentLevelEntry> list = com.google.common.collect.Lists
                    .newArrayList();

            for (int i = 0; i < count; i++) {
                int enchId = buf.readInt();
                int maxLevel = buf.readInt();
                net.minecraft.enchantment.Enchantment enchantment = net.minecraft.registry.Registries.ENCHANTMENT
                        .get(enchId);
                if (enchantment != null) {
                    list.add(new net.minecraft.enchantment.EnchantmentLevelEntry(enchantment, maxLevel));
                }
            }

            client.execute(() -> {
                if (client.player != null
                        && client.player.currentScreenHandler instanceof net.willowins.animewitchery.screen.AlchemicalEnchanterScreenHandler enchanterHandler
                        && enchanterHandler.syncId == syncId) {
                    enchanterHandler.updateClientEnchantmentLists(list);
                    if (client.currentScreen instanceof net.willowins.animewitchery.screen.AlchemicalEnchanterScreen screen) {
                        screen.onEnchantmentsUpdated();
                    }
                }
            });
        });
    }
}