package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

import static net.willowins.animewitchery.world.dimension.ModDimensions.PARADISELOSTDIM_LEVEL_KEY;

public class DimensionTeleportItem extends Item {
    private static final int TELEPORT_MANA_COST = 10000;

    public DimensionTeleportItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Attempt to consume from player's own mana only
            if (!tryConsumeFromPlayer(serverPlayer, TELEPORT_MANA_COST)) {
                user.sendMessage(
                        Text.literal("You need " + TELEPORT_MANA_COST + " mana to teleport.")
                                .formatted(Formatting.RED),
                        true
                );
                return TypedActionResult.fail(user.getStackInHand(hand));
            }

            // Determine teleport destination
            RegistryKey<World> currentDim = serverPlayer.getWorld().getRegistryKey();
            RegistryKey<World> destinationKey = (currentDim == PARADISELOSTDIM_LEVEL_KEY)
                    ? World.OVERWORLD
                    : PARADISELOSTDIM_LEVEL_KEY;

            ServerWorld destinationWorld = serverPlayer.getServer().getWorld(destinationKey);
            if (destinationWorld != null) {
                Vec3d pos = serverPlayer.getPos();

                // Find nearby entities within 2 block radius
                Box searchBox = new Box(pos.subtract(2, 2, 2), pos.add(2, 2, 2));
                List<Entity> nearbyEntities = world.getOtherEntities(serverPlayer, searchBox);

                // Teleport the player first
                serverPlayer.teleport(destinationWorld, pos.x, pos.y, pos.z,
                        serverPlayer.getYaw(), serverPlayer.getPitch());

                // Grant short protective effects
                serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0));

                // Teleport nearby entities
                for (Entity entity : nearbyEntities) {
                    Vec3d entityPos = entity.getPos();
                    entity.moveToWorld(destinationWorld);
                    entity.teleport(entityPos.x, entityPos.y, entityPos.z);
                }

                // Notify player
                if (!nearbyEntities.isEmpty()) {
                    serverPlayer.sendMessage(
                        Text.literal("Teleported " + nearbyEntities.size() + " nearby entities with you!")
                            .formatted(Formatting.AQUA),
                        false
                    );
                }
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    /**
     * Consumes mana directly from the player's personal mana pool.
     * Returns true if sufficient mana was available and consumed.
     */
    private boolean tryConsumeFromPlayer(PlayerEntity player, int cost) {
        IManaComponent mana = ModComponents.PLAYER_MANA.get(player);
        int current = mana.getMana();

        if (current >= cost) {
            mana.consume(cost);
            return true;
        }
        return false;
    }
}
