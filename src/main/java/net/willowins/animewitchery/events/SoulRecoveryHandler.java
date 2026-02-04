package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.willowins.animewitchery.world.SoulRecoveryState;

import java.util.List;

public class SoulRecoveryHandler {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            SoulRecoveryState state = SoulRecoveryState.getServerState(player.getServerWorld());
            if (state == null)
                return;

            List<ItemStack> souls = state.retrieveSouls(player.getUuid());
            if (!souls.isEmpty()) {
                boolean full = false;
                for (ItemStack soul : souls) {
                    if (!player.getInventory().insertStack(soul)) {
                        player.dropItem(soul, false);
                        full = true;
                    }
                }

                player.sendMessage(Text.literal("Your pets' souls have returned to you while you were away.")
                        .formatted(Formatting.GREEN), false);

                if (full) {
                    player.sendMessage(Text.literal("Some souls were dropped nearby (Inventory Full).")
                            .formatted(Formatting.RED), false);
                }
            }
        });
    }
}
