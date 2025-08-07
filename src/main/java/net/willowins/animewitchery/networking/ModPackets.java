package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.lodestone.LaserBeamPacket;
import net.willowins.animewitchery.networking.lodestone.LaserChargePacket;
import net.willowins.animewitchery.networking.lodestone.LaserHitPacket;
import net.willowins.animewitchery.networking.lodestone.ObeliskShake;

public class ModPackets {
    //Lodestone Packets
    public static final Identifier LASER_BEAM = new Identifier(AnimeWitchery.MOD_ID, "laser_beam");
    public static final Identifier LASER_CHARGE = new Identifier(AnimeWitchery.MOD_ID, "laser_charge");
    public static final Identifier LASER_HIT = new Identifier(AnimeWitchery.MOD_ID, "laser_hit");
    public static final Identifier OBELISK_SHAKE = new Identifier(AnimeWitchery.MOD_ID, "obelisk_shake");


    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(LASER_BEAM, LaserBeamPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_CHARGE, LaserChargePacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LASER_HIT, LaserHitPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(OBELISK_SHAKE, ObeliskShake::receive);
    }
}
