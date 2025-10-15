package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;

import java.util.List;

public class RespawnTeleportItem extends Item {

    private static final int FULL_CHARGE_TICKS = 30; // 2 seconds

    public RespawnTeleportItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand); // Begin charging
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000; // Like bow: hold indefinitely
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            if (usedTicks >= FULL_CHARGE_TICKS && serverPlayer.experienceLevel >= 1) {
                BlockPos spawnPos = serverPlayer.getSpawnPointPosition();
                RegistryKey<World> spawnDim = serverPlayer.getSpawnPointDimension();

                ServerWorld destinationWorld;
                Vec3d teleportPos;

                if (spawnPos != null && spawnDim != null) {
                    destinationWorld = serverPlayer.getServer().getWorld(spawnDim);
                    teleportPos = Vec3d.ofCenter(spawnPos);
                } else {
                    destinationWorld = serverPlayer.getServer().getOverworld();
                    teleportPos = Vec3d.ofCenter(destinationWorld.getSpawnPos());
                }

                if (destinationWorld != null) {
                    // Consume XP and teleport
                    serverPlayer.addExperienceLevels(-1);

                    // Find nearby entities within 2 block radius
                    Vec3d currentPos = serverPlayer.getPos();
                    Box searchBox = new Box(currentPos.subtract(2, 2, 2), currentPos.add(2, 2, 2));
                    List<Entity> nearbyEntities = world.getOtherEntities(serverPlayer, searchBox);

                    // Teleport the player first
                    serverPlayer.teleport(destinationWorld,
                            teleportPos.x,
                            teleportPos.y,
                            teleportPos.z,
                            serverPlayer.getYaw(),
                            serverPlayer.getPitch());

                    // Teleport nearby entities
                    for (Entity entity : nearbyEntities) {
                        entity.moveToWorld(destinationWorld);
                        entity.teleport(teleportPos.x, teleportPos.y, teleportPos.z);
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
        }
    }
}