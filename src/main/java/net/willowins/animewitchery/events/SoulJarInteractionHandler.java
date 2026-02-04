package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.willowins.animewitchery.entity.ISummonedEntity;
import net.willowins.animewitchery.item.custom.SoulJarItem;

public class SoulJarInteractionHandler {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && hand == Hand.MAIN_HAND && entity instanceof LivingEntity living) {
                // Check for Empty Main Hand and Soul Jar in Offhand
                if (player.getMainHandStack().isEmpty() && player.getOffHandStack().getItem() instanceof SoulJarItem) {

                    // Check Ownership
                    boolean isMine = false;
                    if (living instanceof net.minecraft.entity.passive.TameableEntity tameable) {
                        if (tameable.getOwnerUuid() != null && tameable.getOwnerUuid().equals(player.getUuid())) {
                            isMine = true;
                        }
                    }
                    if (!isMine) {
                        if (living instanceof ISummonedEntity summoned && summoned.getSummonerUuid() != null) {
                            isMine = summoned.getSummonerUuid().equals(player.getUuid());
                        } else {
                            NbtCompound nbt = new NbtCompound();
                            living.writeNbt(nbt);
                            if (nbt.contains("SummonerOwner")) {
                                isMine = nbt.getUuid("SummonerOwner").equals(player.getUuid());
                            }
                        }
                    }

                    if (isMine) {
                        ItemStack jar = player.getOffHandStack();
                        if (SoulJarItem.captureEntity(player, living, jar)) {
                            living.discard();
                            player.sendMessage(Text.literal("Recalled " + living.getName().getString())
                                    .formatted(Formatting.GREEN), true);
                            return ActionResult.SUCCESS; // Cancel attack and Swing Hand
                        } else {
                            player.sendMessage(Text.literal("Soul Jar is full!").formatted(Formatting.RED), true);
                            return ActionResult.FAIL; // Cancel attack logic but don't success? Or maybe PASS?
                            // If we return FAIL, it cancels the interaction. If we return SUCCESS it
                            // cancels and consumes.
                            // We want to avoid hitting the pet if full, so FAIL/SUCCESS works to cancel
                            // damage.
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
