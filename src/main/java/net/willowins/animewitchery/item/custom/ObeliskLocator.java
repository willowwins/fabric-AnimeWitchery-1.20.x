package net.willowins.animewitchery.item.custom;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.util.ServerScheduler;
import net.willowins.animewitchery.world.ObeliskRegistry;

import java.util.*;

public class ObeliskLocator {

    private static final Map<UUID, ServerBossBar> ACTIVE_BARS = new HashMap<>();

    /** Called from the compass’s right-click use() */
    public static void triggerSearch(ServerWorld world, BlockPos origin, UUID playerId) {
        ServerPlayerEntity player = world.getServer().getPlayerManager().getPlayer(playerId);
        if (player == null) return;

        // Retrieve all known obelisks in this dimension
        Set<BlockPos> obelisks = ObeliskRegistry.get(world).getAll();
        if (obelisks.isEmpty()) {
            player.sendMessage(Text.literal("⚠ No obelisks registered in this dimension!").formatted(Formatting.RED), false);
            return;
        }

        // Create or reuse boss bar
        ServerBossBar bar = ACTIVE_BARS.computeIfAbsent(playerId, id ->
                new ServerBossBar(Text.literal("🔮 Searching for Obelisk..."),
                        BossBar.Color.PURPLE, BossBar.Style.NOTCHED_20));
        bar.clearPlayers();
        bar.addPlayer(player);
        bar.setVisible(true);
        bar.setPercent(0f);

        // Run search asynchronously using your ServerScheduler
        MinecraftServer server = world.getServer();
        List<BlockPos> all = new ArrayList<>(obelisks);
        final int total = all.size();

        ServerScheduler.repeat(server, 1, () -> {
            if (all.isEmpty()) {
                bar.setName(Text.literal("⚠ No Obelisk Found").formatted(Formatting.RED));
                bar.setColor(BossBar.Color.RED);
                bar.setPercent(1f);
                scheduleHide(server, bar, playerId);
                return false;
            }

            // Process a few each tick
            int checksPerTick = 8;
            double bestDistSq = Double.MAX_VALUE;
            BlockPos nearest = null;
            for (int i = 0; i < checksPerTick && !all.isEmpty(); i++) {
                BlockPos candidate = all.remove(0);
                double d2 = origin.getSquaredDistance(candidate);
                if (d2 < bestDistSq) {
                    bestDistSq = d2;
                    nearest = candidate;
                }

                // Update bar progress
                float progress = 1f - (float) all.size() / total;
                bar.setPercent(progress);
            }

            // If we found a candidate very close or finished scanning
            if (all.isEmpty()) {
                if (nearest != null) {
                    ObeliskCompassItem.setTarget(player.getMainHandStack(), nearest, world.getRegistryKey().getValue());
                    player.sendMessage(Text.literal("✨ Obelisk located!").formatted(Formatting.LIGHT_PURPLE), true);
                    bar.setName(Text.literal("✨ Obelisk Found!"));
                    bar.setColor(BossBar.Color.GREEN);
                    bar.setPercent(1f);
                } else {
                    bar.setName(Text.literal("⚠ No Obelisk Found").formatted(Formatting.RED));
                    bar.setColor(BossBar.Color.RED);
                    bar.setPercent(1f);
                }
                scheduleHide(server, bar, playerId);
                return false;
            }

            return true; // continue next tick
        });
    }

    /** Schedule bossbar disappearance */
    private static void scheduleHide(MinecraftServer server, ServerBossBar bar, UUID id) {
        ServerScheduler.schedule(server, 100, () -> {
            bar.clearPlayers();
            bar.setVisible(false);
            ACTIVE_BARS.remove(id);
        });
    }
}
