package net.willowins.animewitchery.networking.lodestone;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.awt.*;

public class ObeliskShake {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        ScreenshakeInstance instance = new ScreenshakeInstance(100)
                .setIntensity(1f, 4f, 0f)
                .setEasing(Easing.SINE_IN_OUT, Easing.EXPO_OUT);

        ScreenshakeHandler.addScreenshake(instance);

    }
}
