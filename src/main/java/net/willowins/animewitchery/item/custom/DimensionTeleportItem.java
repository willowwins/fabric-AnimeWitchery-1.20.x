package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;

import java.util.List;

import static net.willowins.animewitchery.world.dimension.ModDimensions.PARADISELOSTDIM_LEVEL_KEY;

public class DimensionTeleportItem extends Item {
    private static final int TELEPORT_MANA_COST = 1000;

    public DimensionTeleportItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Attempt to consume from player + catalysts
            if (!tryConsumeFromPlayerAndCatalysts(serverPlayer, TELEPORT_MANA_COST)) {
                user.sendMessage(
                        net.minecraft.text.Text.literal("You need " + TELEPORT_MANA_COST + " mana (or stored mana) to teleport")
                                .formatted(net.minecraft.util.Formatting.RED),
                        true
                );
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            // Teleport logic
            RegistryKey<World> currentDim = serverPlayer.getWorld().getRegistryKey();
            RegistryKey<World> destinationKey = (currentDim == PARADISELOSTDIM_LEVEL_KEY)
                    ? World.OVERWORLD
                    : PARADISELOSTDIM_LEVEL_KEY;

            ServerWorld destinationWorld = serverPlayer.getServer().getWorld(destinationKey);
            if (destinationWorld != null) {
                Vec3d pos = serverPlayer.getPos();
                serverPlayer.teleport(destinationWorld,
                        pos.x, pos.y, pos.z,
                        serverPlayer.getYaw(), serverPlayer.getPitch());

                serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0));
                serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0));
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    /**
     * Tries to consume the given cost from player's mana component and then from catalysts in inventory.
     * Returns true if the cost was fully paid; false otherwise (no change if insufficient).
     */
    private boolean tryConsumeFromPlayerAndCatalysts(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int playerMana = mana.getMana();

        if (playerMana >= cost) {
            // enough in player mana alone
            mana.consume(cost);
            return true;
        }

        int remaining = cost - playerMana;
        if (playerMana > 0) {
            mana.consume(playerMana);
        }

        // Now drain from catalysts in inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof AlchemicalCatalystItem) {
                int stored = AlchemicalCatalystItem.getStoredMana(stack);
                if (stored <= 0) continue;
                int take = Math.min(stored, remaining);
                AlchemicalCatalystItem.setStoredMana(stack, stored - take);
                remaining -= take;
                if (remaining <= 0) {
                    break;
                }
            }
        }

        return (remaining <= 0);
    }
}
