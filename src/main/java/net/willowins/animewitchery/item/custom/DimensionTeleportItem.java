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

import static net.willowins.animewitchery.world.dimension.ModDimensions.PARADISELOSTDIM_LEVEL_KEY;

public class DimensionTeleportItem extends Item {

    public DimensionTeleportItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {

            RegistryKey<World> currentDim = serverPlayer.getWorld().getRegistryKey();
            RegistryKey<World> destinationKey = currentDim == PARADISELOSTDIM_LEVEL_KEY
                    ? World.OVERWORLD
                    : PARADISELOSTDIM_LEVEL_KEY;

            ServerWorld destinationWorld = serverPlayer.getServer().getWorld(destinationKey);
            if (destinationWorld != null) {
                Vec3d pos = serverPlayer.getPos();
                serverPlayer.teleport(destinationWorld, pos.x, pos.y, pos.z, serverPlayer.getYaw(), serverPlayer.getPitch());

                // Give status effects on arrival
                serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 40, 0));    // 10 seconds
                serverPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0)); // 10 seconds
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}