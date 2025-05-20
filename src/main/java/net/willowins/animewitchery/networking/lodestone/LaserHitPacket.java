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

public class LaserHitPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        client.execute(() -> {

        for (int i = 0; i < 50; i++) {
                Color startingColor = new Color(150, 15, 160);
                Color endingColor = new Color(246, 10, 255);
                double xr = ( ( 0.5 * Math.random()) - ( ( 0.5 * Math.random() )));
                double zr = ( ( 0.5 * Math.random()) - ( ( 0.5 * Math.random() )));
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(5.0f,0.0f).build())
                        .setTransparencyData(GenericParticleData.create(1.5f, 0f).build())
                        .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                        .setSpinData(SpinParticleData.create((float) (0.2f+(xr*2)), (float) (0.4f+(zr*2))).setSpinOffset((handler.getWorld().getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                        .setLifetime(50)
                        .addMotion(0, 0f, 0)
                        .enableNoClip()
                        .setForceSpawn(true)
                        .setShouldCull(false)
                        .spawn(handler.getWorld(), x+xr , y , z+zr )
                ;

        }

        for (int i = 0; i < 10; i++) {
                Color startingColor = new Color(150, 15, 160);
                Color endingColor = new Color(246, 10, 255);
                double xr = ( ( 1 * Math.random()) - ( ( 1 * Math.random() )));
                double zr = ( ( 1 * Math.random()) - ( ( 1 * Math.random() )));
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(5.0f,0.0f).build())
                        .setTransparencyData(GenericParticleData.create(1.5f, 0f).build())
                        .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                        .setSpinData(SpinParticleData.create((float) (0.2f+(xr*2)), (float) (0.4f+(zr*2))).setSpinOffset((handler.getWorld().getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                        .setLifetime(30)
                        .addMotion(-xr, 0f, -zr)
                        .enableNoClip()
                        .setForceSpawn(true)
                        .setShouldCull(false)
                        .spawn(handler.getWorld(), x+xr , y , z+zr )
                ;

            }
            for (int i = 0; i < 100; i++) {
                Color startingColor = new Color(21, 5, 198);
                Color endingColor = new Color(246, 10, 255);
                double xr = ( ( 1 * Math.random()) - ( ( 1 * Math.random() )));
                double yr = ( ( 1 * Math.random()) - ( ( 1 * Math.random() )));
                double zr = ( ( 1 * Math.random()) - ( ( 1 * Math.random() )));
                WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.1f, 0.75f).build())
                        .setTransparencyData(GenericParticleData.create(1.5f, 0f).build())
                        .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                        .setSpinData(SpinParticleData.create((float) (0.2f+(xr*2)), (float) (0.4f+(zr*2))).setSpinOffset((handler.getWorld().getTime() * 0.2f) % 6.28f).setEasing(Easing.QUARTIC_IN).build())
                        .setLifetime(20)
                        .addMotion(xr, yr, zr)
                        .enableNoClip()
                        .setForceSpawn(true)
                        .setShouldCull(false)
                        .spawn(handler.getWorld(), x+xr , y+yr , z+zr )
                ;

            }
        if (Math.abs(x - client.player.getPos().x) <= 200 && Math.abs(z - client.player.getPos().z) <= 200) {
            ScreenshakeHandler.addScreenshake(new ScreenshakeInstance(5).setIntensity(0.1f));
        }
    });
    }
}
