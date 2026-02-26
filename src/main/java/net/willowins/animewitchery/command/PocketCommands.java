package net.willowins.animewitchery.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import net.willowins.animewitchery.world.dimension.ModDimensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.willowins.animewitchery.world.dimension.PocketManager;

public class PocketCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment) {
        // /pocketlist - Lists all pocket dimensions
        dispatcher.register(CommandManager.literal("pocketlist")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(PocketCommands::executePocketList));

        // /pocket <id> - Teleport to specific pocket
        dispatcher.register(CommandManager.literal("pocket")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("id", IntegerArgumentType.integer(0))
                        .executes(context -> executePocketTeleport(context,
                                IntegerArgumentType.getInteger(context, "id")))));
    }

    private static int executePocketList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        try {
            ServerWorld pocketWorld = source.getServer().getWorld(ModDimensions.POCKET_LEVEL_KEY);
            if (pocketWorld == null) {
                source.sendError(Text.literal("Pocket dimension world not found.").formatted(Formatting.RED));
                return 0;
            }

            PocketManager manager = PocketManager.getServerState(pocketWorld);
            List<Integer> activeIds = manager.getAllActivePocketIds();

            if (activeIds.isEmpty()) {
                source.sendFeedback(() -> Text.literal("No active pocket dimensions found.")
                        .formatted(Formatting.YELLOW), false);
                return 0;
            }

            source.sendFeedback(
                    () -> Text.literal("=== Active Pocket Dimensions (" + activeIds.size() + ") ===")
                            .formatted(Formatting.GOLD),
                    false);

            for (int id : activeIds) {
                // Optional: differentiate legacy vs dynamic if useful, or just list IDs
                source.sendFeedback(
                        () -> Text.literal("- Pocket ID: " + id).formatted(Formatting.GREEN)
                                .append(Text
                                        .literal(" (UUID: " + manager.getUuidForId(id).toString().substring(0, 8)
                                                + "...)")
                                        .formatted(Formatting.DARK_GRAY)),
                        false);
            }

            return activeIds.size();
        } catch (Exception e) {
            source.sendError(Text.literal("Error listing pockets: " + e.getMessage()).formatted(Formatting.RED));
            return 0;
        }
    }

    private static int executePocketTeleport(CommandContext<ServerCommandSource> context, int pocketId)
            throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();

        try {
            // Unlocked ID limit - accept any positive ID
            if (pocketId < 0) {
                source.sendError(Text.literal("Pocket ID must be positive.").formatted(Formatting.RED));
                return 0;
            }

            ServerWorld pocketWorld = source.getServer().getWorld(ModDimensions.POCKET_LEVEL_KEY);
            if (pocketWorld == null) {
                source.sendError(Text.literal("Pocket dimension world not found.").formatted(Formatting.RED));
                return 0;
            }

            // Calculate spawn position for this pocket ID
            BlockPos spawnPos = PocketManager.getPocketCenter(pocketId);

            // Check for custom spawn override
            PocketManager manager = PocketManager.getServerState(pocketWorld);
            BlockPos customSpawn = manager.getCustomSpawn(pocketId);
            if (customSpawn != null) {
                spawnPos = customSpawn;
                source.sendFeedback(() -> Text.literal("Found custom spawn in pocket " + pocketId)
                        .formatted(Formatting.YELLOW), false);
            }

            // Ensure safe spawn platform
            PocketManager.ensureSpawnPlatform(pocketWorld, spawnPos);

            // Teleport player
            player.teleport(pocketWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);

            // Sync World Border (No longer needed with new Grid Collision system)
            // PocketManager.sendBorderPacket(player, pocketId);

            source.sendFeedback(
                    () -> Text.literal("Teleported to pocket dimension " + pocketId).formatted(Formatting.GREEN), true);
            return 1;

        } catch (Exception e) {
            source.sendError(Text.literal("Error teleporting to pocket: " + e.getMessage()).formatted(Formatting.RED));
            return 0;
        }
    }
}
