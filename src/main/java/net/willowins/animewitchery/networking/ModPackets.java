package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.lodestone.*;

public class ModPackets {
    //Lodestone Packets
    public static final Identifier LASER_BEAM = new Identifier(AnimeWitchery.MOD_ID, "laser_beam");
    public static final Identifier LASER_CHARGE = new Identifier(AnimeWitchery.MOD_ID, "laser_charge");
    public static final Identifier LASER_HIT = new Identifier(AnimeWitchery.MOD_ID, "laser_hit");
    public static final Identifier OBELISK_SHAKE = new Identifier(AnimeWitchery.MOD_ID, "obelisk_shake");
    public static final Identifier KAMIKAZE_FX = new Identifier(AnimeWitchery.MOD_ID, "kamikaze_fx");
    public static final Identifier CREATE_PLATFORM_ID = new Identifier("animewitchery", "create_platform");


    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(KAMIKAZE_FX, KamikazeFxPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_BEAM, LaserBeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_CHARGE, LaserChargePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_HIT, LaserHitPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(OBELISK_SHAKE, ObeliskShake::receive);
        ServerPlayNetworking.registerGlobalReceiver(CREATE_PLATFORM_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player != null) {
                    net.willowins.animewitchery.events.PlatformCreator.tryCreatePlatform(player);
                }
            });
        });
    }}