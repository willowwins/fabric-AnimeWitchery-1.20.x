package net.willowins.animewitchery.item.custom;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.mana.ManaStorageRegistry;

import java.util.UUID;

/**
 * The Unban Wand — channels mana through the subvoid.
 * After 3 seconds of charge, release to attempt a Contract (unban).
 */
public class UnbanWandItem extends Item {
    private static final int USE_MANA_COST = 30000;
    private static final int CHARGE_DURATION = 60; // 3 seconds (20 ticks * 3)

    public UnbanWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        // Allow indefinite charging (standard bow behavior)
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    /**
     * Tick while charging — optional visual or auditory feedback may be placed here.
     */
    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient) return;
        if (!(user instanceof ServerPlayerEntity serverPlayer)) return;

        int usedTicks = getMaxUseTime(stack) - remainingUseTicks;

        if (usedTicks == CHARGE_DURATION) {
            serverPlayer.sendMessage(
                    Text.literal("⚡ The subvoid thrums beneath your grasp... release to invoke the Contract.")
                            .formatted(Formatting.DARK_PURPLE),
                    true
            );
        }
    }

    /**
     * Called when the player releases right-click (stops charging).
     */
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        if (world.isClient) return;
        if (!(entity instanceof ServerPlayerEntity serverPlayer)) return;

        int usedTicks = getMaxUseTime(stack) - remainingUseTicks;

        // Require at least 3 seconds of charge
        if (usedTicks < CHARGE_DURATION) {
            serverPlayer.sendMessage(
                    Text.literal("The wand whispers: 'Reach further through the subvoid…'")
                            .formatted(Formatting.GRAY),
                    false
            );
            return;
        }

        // Attempt mana consumption
        if (!consumeManaWithStorage(serverPlayer, USE_MANA_COST)) {
            serverPlayer.sendMessage(
                    Text.literal("Your essence falters. The void demands " + USE_MANA_COST + " mana.")
                            .formatted(Formatting.RED),
                    false
            );
            return;
        }

        performContract(world, serverPlayer, stack);
    }

    private void performContract(World world, ServerPlayerEntity caster, ItemStack stack) {
        String targetName = stack.getName().getString().trim();
        MinecraftServer server = caster.getServer();
        if (server == null) return;

        var banList = server.getPlayerManager().getUserBanList();
        UUID targetUUID = server.getUserCache()
                .findByName(targetName)
                .map(GameProfile::getId)
                .orElse(null);

        if (targetUUID == null) {
            caster.sendMessage(
                    Text.literal("No trace of " + targetName + " can be found within the subvoid.")
                            .formatted(Formatting.RED),
                    false
            );
            return;
        }

        GameProfile profile = new GameProfile(targetUUID, targetName);
        if (banList.get(profile) != null) {
            banList.remove(profile);
            server.getPlayerManager().broadcast(
                    Text.literal("✦ " + targetName + " has been Contracted by " + caster.getName().getString() + "!")
                            .formatted(Formatting.DARK_AQUA),
                    false
            );

            // Subvoid shockwave
            if (world instanceof ServerWorld serverWorld) {
                Vec3d center = caster.getPos();
                double radius = 5.0;
                for (ServerPlayerEntity p : serverWorld.getPlayers()) {
                    if (p.squaredDistanceTo(center) < radius * radius) {
                        Vec3d dir = p.getPos().subtract(center).normalize().multiply(1.5);
                        p.addVelocity(dir.x, 0.6, dir.z);
                        p.velocityModified = true;
                    }
                }
            }

            stack.decrement(1);
        } else {
            caster.sendMessage(
                    Text.literal(targetName + " is not currently susceptible to a Contract.")
                            .formatted(Formatting.GRAY),
                    false
            );
        }
    }

    /**
     * Consumes mana from the player's personal pool and from registered mana storage items.
     */
    private boolean consumeManaWithStorage(PlayerEntity player, int totalCost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int remaining = totalCost;

        // Step 1: Player’s personal mana
        int playerMana = mana.getMana();
        if (playerMana > 0) {
            int consume = Math.min(playerMana, remaining);
            mana.consume(consume);
            remaining -= consume;
        }

        // Step 2: Stored mana from catalysts
        if (remaining > 0) {
            int totalStored = ManaStorageRegistry.getStoredManaFromItems(player);
            if (totalStored > 0) {
                ManaStorageRegistry.consumeFromStorage(player, remaining);
                remaining -= Math.min(totalStored, remaining);
            }
        }

        return remaining <= 0;
    }
}
