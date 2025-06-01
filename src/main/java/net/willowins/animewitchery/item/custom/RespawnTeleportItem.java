package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;

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
                    serverPlayer.teleport(destinationWorld,
                            teleportPos.x,
                            teleportPos.y,
                            teleportPos.z,
                            serverPlayer.getYaw(),
                            serverPlayer.getPitch());
                }
            }
        }
    }
}