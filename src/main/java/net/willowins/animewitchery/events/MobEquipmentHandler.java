package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.entity.ISummonedEntity;

import java.util.Collections;
import com.mojang.datafixers.util.Pair;

public class MobEquipmentHandler {

    // Debounce map to prevent accidental double-clicks (0.4s cooldown)
    private static final java.util.WeakHashMap<PlayerEntity, Long> COOLDOWNS = new java.util.WeakHashMap<>();

    public static void register() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient || hand != Hand.MAIN_HAND)
                return ActionResult.PASS;

            // Debounce Check (8 ticks = 0.4 seconds)
            if (COOLDOWNS.containsKey(player) && world.getTime() - COOLDOWNS.get(player) < 8) {
                return ActionResult.PASS;
            }

            if (!(entity instanceof LivingEntity living) || !(entity instanceof MobEntity mob))
                return ActionResult.PASS;

            // 1. Verify Ownership
            if (!isOwner(player, living))
                return ActionResult.PASS;

            ItemStack playerStack = player.getMainHandStack();

            // 2. Interaction Logic
            if (playerStack.isEmpty()) {
                return handleTakeItem(player, mob, world);
            } else {
                return handleEquipItem(player, mob, playerStack, world);
            }
        });
    }

    private static boolean isOwner(PlayerEntity player, LivingEntity entity) {
        if (entity instanceof TameableEntity tameable) {
            return tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(player.getUuid());
        }
        if (entity instanceof ISummonedEntity summoned && summoned.getSummonerUuid() != null) {
            return summoned.getSummonerUuid().equals(player.getUuid());
        }
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        return nbt.contains("SummonerOwner") && nbt.getUuid("SummonerOwner").equals(player.getUuid());
    }

    private static ActionResult handleTakeItem(PlayerEntity player, MobEntity mob, net.minecraft.world.World world) {
        // Priority: Main -> Off -> Head -> Chest -> Legs -> Feet
        EquipmentSlot[] slotsToCheck = {
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND,
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        EquipmentSlot slot = null;
        ItemStack mobStack = ItemStack.EMPTY;

        for (EquipmentSlot s : slotsToCheck) {
            ItemStack stack = mob.getEquippedStack(s);
            if (!stack.isEmpty()) {
                slot = s;
                mobStack = stack;
                break;
            }
        }

        if (slot != null && !mobStack.isEmpty()) {
            ItemStack toDrop = mobStack.copy();

            // CRITICAL: Force clear
            mob.equipStack(slot, new ItemStack(Items.AIR));

            // Verify: If the mob is STILL holding it, abort.
            if (!mob.getEquippedStack(slot).isEmpty()) {
                return ActionResult.PASS;
            }

            // Cleanup logic
            mob.setEquipmentDropChance(slot, 0.085f);
            mob.setCanPickUpLoot(false);

            // DROP ITEM instead of giving to player
            mob.dropStack(toDrop);

            // Sync: Force updates
            syncEquipment(mob, slot, ItemStack.EMPTY, world);

            player.sendMessage(Text.literal("Dropped " + toDrop.getName().getString()).formatted(Formatting.GREEN),
                    true);
            world.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                    SoundCategory.PLAYERS, 1.0f, 1.0f);
            mob.swingHand(Hand.MAIN_HAND, true);

            if (mob instanceof AbstractSkeletonEntity skeleton)
                skeleton.updateAttackType();

            mob.setPersistent();

            // Set Cooldown
            COOLDOWNS.put(player, world.getTime());

            return ActionResult.SUCCESS;
        }
        // If nothing to take, return PASS
        return ActionResult.PASS;
    }

    private static ActionResult handleEquipItem(PlayerEntity player, MobEntity mob, ItemStack playerStack,
            net.minecraft.world.World world) {
        EquipmentSlot slot = getPreferredSlot(playerStack);
        ItemStack currentMobStack = mob.getEquippedStack(slot);

        if (!currentMobStack.isEmpty()) {
            player.sendMessage(Text.literal("Hands (or Slot) are full! Drop the item first.").formatted(Formatting.RED),
                    true);
            // CRITICAL: Return SUCCESS to stop Vanilla from trying to swap
            return ActionResult.SUCCESS;
        }

        // Cache name before consuming
        String itemName = playerStack.getName().getString();

        ItemStack toEquip = playerStack.copy();
        toEquip.setCount(1);

        // Equip
        mob.equipStack(slot, toEquip);

        // Config: Lock it in
        mob.setCanPickUpLoot(false);
        mob.setEquipmentDropChance(slot, 2.0f);
        mob.setPersistent();

        // Consume from player (Verify decrement)
        if (!player.isCreative()) {
            playerStack.decrement(1);
        }

        // Sync
        syncEquipment(mob, slot, toEquip, world);

        if (mob instanceof AbstractSkeletonEntity skeleton)
            skeleton.updateAttackType();

        player.sendMessage(Text.literal("Equipped " + itemName).formatted(Formatting.GREEN), true);
        world.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                SoundCategory.PLAYERS, 1.0f, 1.0f);
        mob.swingHand(Hand.MAIN_HAND, true);

        // Set Cooldown
        COOLDOWNS.put(player, world.getTime());

        return ActionResult.SUCCESS;
    }

    private static EquipmentSlot getPreferredSlot(ItemStack stack) {
        if (stack.getItem() instanceof ArrowItem)
            return EquipmentSlot.OFFHAND;
        return LivingEntity.getPreferredEquipmentSlot(stack);
    }

    private static void syncEquipment(MobEntity mob, EquipmentSlot slot, ItemStack stack,
            net.minecraft.world.World world) {
        if (world instanceof ServerWorld serverWorld) {
            EntityEquipmentUpdateS2CPacket packet = new EntityEquipmentUpdateS2CPacket(
                    mob.getId(),
                    Collections.singletonList(Pair.of(slot, stack)));
            serverWorld.getChunkManager().sendToOtherNearbyPlayers(mob, packet);

            if (world.getClosestPlayer(mob, 5) instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(packet);
            }
        }
    }
}
