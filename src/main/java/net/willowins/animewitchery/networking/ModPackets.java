package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.lodestone.LaserBeamPacket;

public class ModPackets {
    //Lodestone Packets
    public static final Identifier LASER_BEAM = new Identifier(AnimeWitchery.MOD_ID, "laser_beam");


    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(LASER_BEAM, LaserBeamPacket::receive);
    }
}
