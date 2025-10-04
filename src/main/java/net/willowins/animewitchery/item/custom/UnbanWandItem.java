package net.willowins.animewitchery.item.custom;

import com.mojang.authlib.GameProfile;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;

import java.util.UUID;

public class UnbanWandItem extends Item {
    private static final int USE_MANA_COST = 30000;

    public UnbanWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        if (!(user instanceof ServerPlayerEntity serverPlayer)) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        // Check and consume mana (from player + catalysts)
        if (!tryConsumeFromPlayerAndCatalysts(serverPlayer, USE_MANA_COST)) {
            serverPlayer.sendMessage(
                    Text.literal("You need " + USE_MANA_COST + " mana (or stored in catalysts) to use the wand").formatted(Formatting.RED),
                    false
            );
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        ItemStack stack = user.getStackInHand(hand);
        String targetName = stack.getName().getString().trim();

        MinecraftServer server = serverPlayer.getServer();
        if (server == null) {
            return TypedActionResult.fail(stack);
        }

        var banList = server.getPlayerManager().getUserBanList();
        UUID targetUUID = server.getUserCache()
                .findByName(targetName)
                .map(GameProfile::getId)
                .orElse(null);
        if (targetUUID == null) {
            serverPlayer.sendMessage(
                    Text.literal("❌ Could not find UUID for player name '" + targetName + "'").formatted(Formatting.RED),
                    false
            );
            return TypedActionResult.fail(stack);
        }

        GameProfile unbanProfile = new GameProfile(targetUUID, targetName);
        if (banList.get(unbanProfile) != null) {
            banList.remove(unbanProfile);
            server.getPlayerManager().broadcast(
                    Text.literal("✨ " + targetName + " has been unbanned by " + user.getName().getString() + "!")
                            .formatted(Formatting.GREEN),
                    false
            );

            if (world instanceof ServerWorld serverWorld) {
                double radius = 5.0;
                Vec3d center = user.getPos();
                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    if (player.squaredDistanceTo(center) < radius * radius) {
                        Vec3d direction = player.getPos().subtract(center).normalize().multiply(1.5);
                        player.addVelocity(direction.x, 0.5, direction.z);
                        player.velocityModified = true;
                    }
                }
            }
            stack.decrement(1);
            return TypedActionResult.success(stack, world.isClient());
        } else {
            serverPlayer.sendMessage(
                    Text.literal("❌ No banned player found with the name '" + targetName + "'").formatted(Formatting.RED),
                    false
            );
            return TypedActionResult.fail(stack);
        }
    }

    /**
     * Attempts to consume the given cost from player's mana component and catalysts.
     * Returns true if fully paid, false otherwise.
     */
    private boolean tryConsumeFromPlayerAndCatalysts(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int playerMana = mana.getMana();
        if (playerMana >= cost) {
            mana.consume(cost);
            return true;
        }

        int remaining = cost - playerMana;
        if (playerMana > 0) {
            mana.consume(playerMana);
        }

        // Drain from catalysts
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof AlchemicalCatalystItem) {
                int stored = AlchemicalCatalystItem.getStoredMana(stack);
                if (stored <= 0) continue;
                int take = Math.min(stored, remaining);
                AlchemicalCatalystItem.setStoredMana(stack, stored - take);
                remaining -= take;
                if (remaining <= 0) break;
            }
        }

        return (remaining <= 0);
    }
}
