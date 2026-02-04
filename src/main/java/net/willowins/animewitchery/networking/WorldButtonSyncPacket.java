package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.AnimeWitchery;

public class WorldButtonSyncPacket {

    public static final Identifier SET_WORLD_ID = new Identifier(AnimeWitchery.MOD_ID, "set_world");
    public static final Identifier SET_SERVER_ID = new Identifier(AnimeWitchery.MOD_ID, "set_server");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SET_WORLD_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String worldName = buf.readString();

            server.execute(() -> {
                if (player.getWorld().getBlockEntity(
                        pos) instanceof net.willowins.animewitchery.block.entity.WorldButtonBlockEntity be) {
                    be.setWorldName(worldName);
                    player.sendMessage(net.minecraft.text.Text.literal("§aWorld set to: §f" + worldName), false);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SET_SERVER_ID, (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String serverName = buf.readString();
            String serverIP = buf.readString();

            server.execute(() -> {
                if (player.getWorld().getBlockEntity(
                        pos) instanceof net.willowins.animewitchery.block.entity.ServerButtonBlockEntity be) {
                    be.setServerName(serverName);
                    be.setServerIP(serverIP);
                    player.sendMessage(net.minecraft.text.Text
                            .literal("§bServer set to: §f" + serverName + " §7(" + serverIP + ")"), false);
                }
            });
        });
    }

    public static void sendSetWorld(BlockPos pos, String worldName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(worldName);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(SET_WORLD_ID, buf);
    }

    public static void sendSetServer(BlockPos pos, String serverName, String serverIP) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeString(serverName);
        buf.writeString(serverIP);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(SET_SERVER_ID, buf);
    }
}
