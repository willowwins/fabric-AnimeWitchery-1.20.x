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
import org.joml.Vector3f;
import net.willowins.animewitchery.particle.ModParticles;

import java.awt.*;

public class RailgunBeamPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf,
            PacketSender packetSender) {
        // Read start position
        double x1 = buf.readDouble();
        double y1 = buf.readDouble();
        double z1 = buf.readDouble();

        // Read end position
        double x2 = buf.readDouble();
        double y2 = buf.readDouble();
        double z2 = buf.readDouble();

        client.execute(() -> {
            if (client.world == null)
                return;

            // Vector math
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Normalize direction
            double nx = dx / distance;
            double ny = dy / distance;
            double nz = dz / distance;

            // Colors
            Color purpleCore = new Color(138, 43, 226); // BlueViolet
            Color purpleOuter = new Color(75, 0, 130); // Indigo
            Color sparkleColor = new Color(255, 215, 0); // Gold

            // Iterate along the beam
            for (double i = 0; i < distance; i += 0.5) {
                double cx = x1 + nx * i;
                double cy = y1 + ny * i;
                double cz = z1 + nz * i;

                // 1. Core Beam (Purple Wisp) - Thicker, glowing
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.4f, 0).build())
                        .setTransparencyData(GenericParticleData.create(1.0f, 0).build())
                        .setColorData(ColorParticleData.create(purpleCore, purpleOuter).build())
                        .setLifetime(20)
                        .enableNoClip()
                        .spawn(client.world, cx, cy, cz);

                // 2. Spiral Sparkles
                // Calculate spiral offset
                double time = client.world.getTime() * 0.1;
                double angle = (i * 0.5) + time;
                double radius = 0.3;

                // Construct a basis for the perpendicular plane (simplified, assumes primarily
                // Y-axis irrelevant or handled roughly)
                // Proper way is cross product, but for visual chaos simple trig often works or
                // use Joml
                // Let's use a simple distinct perpendicular vector hack

                // But to make it robust, we can use Lodestone's own or simple math:
                // Just randomize slightly around the center for now to ensure we don't break
                // logic with complex quaternion math
                // Or better: Use two perpendicular vectors u and v.

                // Let's settle for a "Cloud" around the beam instead of perfect spiral to avoid
                // complex math bugs in this one-shot
                // User asked for "Spiral", so I should try.

                // Approximate Up vector
                double ux = 0, uy = 1, uz = 0;
                if (Math.abs(ny) > 0.9) {
                    ux = 1;
                    uy = 0;
                } // If looking mostly up/down, use X as up

                // Cross product for Right vector (Beam x Up)
                double rx = ny * uz - nz * uy;
                double ry = nz * ux - nx * uz;
                double rz = nx * uy - ny * ux;

                // Normalize Right
                double rLen = Math.sqrt(rx * rx + ry * ry + rz * rz);
                rx /= rLen;
                ry /= rLen;
                rz /= rLen;

                // Cross product for true Up (Right x Beam)
                ux = ry * nz - rz * ny;
                uy = rz * nx - rx * nz;
                uz = rx * ny - ry * nx;

                // Spiral position
                double sx = cx + (rx * Math.cos(angle) + ux * Math.sin(angle)) * radius;
                double sy = cy + (ry * Math.cos(angle) + uy * Math.sin(angle)) * radius;
                double sz = cz + (rz * Math.cos(angle) + uz * Math.sin(angle)) * radius;

                WorldParticleBuilder.create(LodestoneParticleRegistry.SPARKLE_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.15f, 0).build())
                        .setColorData(ColorParticleData.create(sparkleColor, purpleCore).build())
                        .setLifetime(25)
                        .enableNoClip()
                        .spawn(client.world, sx, sy, sz);
            }

            // Screen shake at the player
            if (client.player != null && client.player.squaredDistanceTo(x1, y1, z1) < 100) {
                ScreenshakeHandler.addScreenshake(new ScreenshakeInstance(10).setIntensity(1.5f));
            }
            // 3. Original Shockwave Particles (Muzzle Flash)
            // Replicates the original RailgunItem logic: 3 bursts at 4, 8, 12 blocks
            // distance
            for (int i = 1; i <= 3; i++) {
                double sx = x1 + (4 * i * nx);
                double sy = y1 + (4 * i * ny);
                double sz = z1 + (4 * i * nz);

                for (int j = 0; j < 5; j++) {
                    client.world.addParticle(ModParticles.LASER_PARTICLE,
                            sx, sy, sz,
                            0, 0.02, 0); // Original had 0.02 speed, but addParticle takes velocity directly.
                                         // ServerWorld.spawnParticles with count > 0 uses velocity/delta differently.
                                         // For ClientWorld.addParticle, we pass vx, vy, vz.
                                         // The original was spawnParticles(..., 5, 0, 0, 0, 0.02);
                                         // This usually means random spread if count > 0.
                                         // Let's approximate the "explosion" look.
                }
                // Actually, ShockwaveParticle ignores velocity in its constructor except for
                // rotation?
                /*
                 * ShockwaveParticle constructor:
                 * this.velocityX = 0.5*direction.x;
                 * this.velocityY = 0.5*direction.y;
                 * this.velocityZ = 0.5*direction.z;
                 * ...
                 * So it uses the passed velocity as direction!
                 */
                client.world.addParticle(ModParticles.LASER_PARTICLE, sx, sy, sz, nx, ny, nz);
            }
        });
    }
}
