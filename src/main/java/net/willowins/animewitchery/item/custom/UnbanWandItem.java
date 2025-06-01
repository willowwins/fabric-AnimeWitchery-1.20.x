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

import java.util.UUID;

public class UnbanWandItem extends Item {

    public UnbanWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));
        if (!(user instanceof ServerPlayerEntity serverPlayer)) return TypedActionResult.pass(user.getStackInHand(hand));

        ItemStack stack = user.getStackInHand(hand);
        String targetName = stack.getName().getString().trim();

        MinecraftServer server = serverPlayer.getServer();
        if (server == null) return TypedActionResult.fail(stack);

        var banList = server.getPlayerManager().getUserBanList();

        // Lookup UUID for the target player name
        UUID targetUUID = server.getUserCache().findByName(targetName).map(GameProfile::getId).orElse(null);
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
            stack.decrement(1); // consume the item
            return TypedActionResult.success(stack, world.isClient());
        } else {
            serverPlayer.sendMessage(
                    Text.literal("❌ No banned player found with the name '" + targetName + "'").formatted(Formatting.RED),
                    false
            );
            return TypedActionResult.fail(stack);
        }
    }
}
