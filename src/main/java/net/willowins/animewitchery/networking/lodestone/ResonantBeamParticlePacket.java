package net.willowins.animewitchery.networking.lodestone;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import java.awt.Color;

public class ResonantBeamParticlePacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
            PacketSender packetSender) {
        // Read data
        double x1 = buf.readDouble();
        double y1 = buf.readDouble();
        double z1 = buf.readDouble();
        double x2 = buf.readDouble();
        double y2 = buf.readDouble();
        double z2 = buf.readDouble();
        float radius = buf.readFloat(); // Read radius
        float progress = buf.readFloat();

        client.execute(() -> {
            if (client.world == null)
                return;

            // Geometry
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            double nx = dx / distance;
            double ny = dy / distance;
            double nz = dz / distance;

            // Visual Params
            Color purpleCore = new Color(138, 43, 226);
            Color purpleOuter = new Color(75, 0, 130);
            Color sparkleColor = new Color(50, 255, 50); // Green Sparkles
            float sizeMultiplier = 1.0f + progress * 2.0f; // Scale up to 3x size

            // Render wider beam based on radius
            int strands = 1 + (int) (radius * 3); // More strands for wider beam

            // Spawn Particles
            for (double i = 0; i < distance; i += 1.0) {
                double cx = x1 + nx * i;
                double cy = y1 + ny * i;
                double cz = z1 + nz * i;

                // Spread particles within radius cylinder
                for (int s = 0; s < strands; s++) {
                    double offsetX = (client.world.random.nextDouble() - 0.5) * radius * 0.8;
                    double offsetY = (client.world.random.nextDouble() - 0.5) * radius * 0.8;
                    double offsetZ = (client.world.random.nextDouble() - 0.5) * radius * 0.8;

                    // Core Beam (with spread)
                    WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.3f * sizeMultiplier, 0).build())
                            .setTransparencyData(GenericParticleData.create(0.6f, 0).build()) // Slightly more
                                                                                              // transparent
                            .setColorData(ColorParticleData.create(purpleCore, purpleOuter).build())
                            .setLifetime(10 + client.world.random.nextInt(5))
                            .enableNoClip()
                            .spawn(client.world, cx + offsetX, cy + offsetY, cz + offsetZ);
                }

                // Sparkles (Wider spread)
                if (client.world.random.nextFloat() < (0.25f + progress * 0.5f)) {
                    double offsetX = (client.world.random.nextDouble() - 0.5) * radius * 1.5;
                    double offsetY = (client.world.random.nextDouble() - 0.5) * radius * 1.5;
                    double offsetZ = (client.world.random.nextDouble() - 0.5) * radius * 1.5;

                    WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                            .setScaleData(GenericParticleData.create(0.1f * sizeMultiplier, 0).build())
                            .setColorData(ColorParticleData.create(sparkleColor, purpleCore).build())
                            .setLifetime(20)
                            .enableNoClip()
                            .spawn(client.world, cx + offsetX, cy + offsetY, cz + offsetZ);
                }
            }
        });
    }
}
