package net.willowins.animewitchery.item.custom;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.BanEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.willowins.animewitchery.AnimeWitchery;

public class BanHammerItem extends Item {

    public BanHammerItem(Settings settings) {
        super(settings);
    }

    public static void tryBanOnKill(PlayerEntity killed, DamageSource source) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity killer)) return;
        if (!(killed instanceof ServerPlayerEntity victim)) return;

        ItemStack offhand = killer.getOffHandStack();
        if (!(offhand.getItem() instanceof BanHammerItem)) return;

        MinecraftServer server = killer.getServer();
        if (server != null) {
            String banCommand = "ban " + victim.getGameProfile().getName() + " Returned to Stardust";

            CommandDispatcher<ServerCommandSource> dispatcher = (CommandDispatcher<ServerCommandSource>) (Object) server.getCommandManager();

            try {
                dispatcher.execute(banCommand, server.getCommandSource());
            } catch (CommandSyntaxException e) {
                AnimeWitchery.LOGGER.error("Failed to ban player", e);// he error with a logger if you use one
                return;
            }

            ServerPlayNetworkHandler handler = victim.networkHandler;
            if (handler != null) {
                handler.disconnect(Text.literal("Returned to Stardust").formatted(Formatting.DARK_PURPLE));
            }

            server.getPlayerManager().broadcast(Text.literal(victim.getGameProfile().getName() + " Was returned to Stardust").formatted(Formatting.DARK_PURPLE), false);
        }
    }
}
