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

public class LaserBeamPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        client.execute(() -> {

        for (int i = 0; i < 10; i++) {
                Color startingColor = new Color(150, 15, 160);
                Color endingColor = new Color(246, 10, 255);
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.5f, 1f,0.0f).build())
                        .setTransparencyData(GenericParticleData.create(1.5f, 0f).build())
                        .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                        .setSpinData(SpinParticleData.create(0.2f, 0.4f).setSpinOffset((handler.getWorld().getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                        .setLifetime(20)
                        .addMotion(0, 0f, 0)
                        .enableNoClip()
                        .setForceSpawn(true)
                        .setShouldCull(false)
                        .spawn(handler.getWorld(), x+( ( 0.25 * Math.random()) - ( ( 0.25 * Math.random() ))) , y+0.5 , z+ ( ( 0.25 * Math.random()) - ( 0.25 * Math.random() )) )
                ;

        }
        if (Math.abs(x - client.player.getPos().x) <= 30 && Math.abs(z - client.player.getPos().z) <= 30) {
            ScreenshakeHandler.addScreenshake(new ScreenshakeInstance(10).setIntensity(2));
        }
    });
    }
}
