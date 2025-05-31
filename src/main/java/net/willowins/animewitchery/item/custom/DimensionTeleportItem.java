package net.willowins.animewitchery.item.custom;

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
            ServerWorld currentWorld = serverPlayer.getServerWorld();
            RegistryKey<World> destinationKey;

            // Toggle between Overworld and Paradise Lost
            if (currentWorld.getRegistryKey() == World.OVERWORLD) {
                destinationKey = PARADISELOSTDIM_LEVEL_KEY;
            } else if (currentWorld.getRegistryKey() == PARADISELOSTDIM_LEVEL_KEY) {
                destinationKey = World.OVERWORLD;
            } else {
                return TypedActionResult.fail(serverPlayer.getStackInHand(hand));
            }

            ServerWorld destinationWorld = serverPlayer.getServer().getWorld(destinationKey);
            if (destinationWorld != null) {
                Vec3d pos = serverPlayer.getPos();
                serverPlayer.teleport(destinationWorld, pos.x, pos.y, pos.z, serverPlayer.getYaw(), serverPlayer.getPitch());
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
