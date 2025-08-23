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

public class KamikazeFxPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final double cx = buf.readDouble();
        final double cy = buf.readDouble();
        final double cz = buf.readDouble();
        final float radius     = buf.readFloat();
        final float maxRadius  = buf.readFloat();
        final float progress   = buf.readFloat(); // 0..1
        final int seed         = buf.readInt();
        final boolean bigPulse = buf.readBoolean();

        client.execute(() -> {
            // ---- TIMELINE CHOREOGRAPHY ----
            if (progress < 0.05f) {
                spawnFlashBloom(client.world, cx, cy, cz, radius, maxRadius, seed);
                spawnImplosionSwirl(client.world, cx, cy, cz, radius, seed);
            } else if (progress < 0.25f) {
                spawnDustWall(client.world, cx, cy, cz, radius, seed);
                if (bigPulse) spawnLightningRimArcs(client.world, cx, cy, cz, radius, seed, 6);
            } else if (progress < 0.60f) {
                spawnMushroomColumn(client.world, cx, cy, cz, radius, seed);
                if (bigPulse) spawnPressureBursts(client.world, cx, cy, cz, radius, seed);
            } else {
                spawnAshFallout(client.world, cx, cy, cz, radius, seed);
                if (bigPulse) spawnRuneRing(client.world, cx, cy, cz, radius * 0.92, seed);
            }
        });
    }

    private static void spawnFlashBloom(net.minecraft.client.world.ClientWorld world,
                                        double cx, double cy, double cz,
                                        float radius, float maxRadius, int seed) {
        // Brief camera kick (a touch more than your base shake)
        ScreenshakeHandler.addScreenshake(new ScreenshakeInstance(6).setIntensity(0.5f));

        // Central overbright wisps and stars
        int count = 120;
        Color hotA = new Color(255, 240, 255);
        Color hotB = new Color(255, 120, 240);
        for (int i = 0; i < count; i++) {
            double a1 = hash(seed ^ 0x111, i) * Math.PI * 2.0;
            double r  = 0.6 + hash(seed ^ 0x222, i) * 1.2;
            double x  = cx + Math.cos(a1) * r;
            double z  = cz + Math.sin(a1) * r;
            double y  = cy + 0.1 + (hash(seed ^ 0x333, i) - 0.5) * 0.6;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(1.2f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(1.2f, 0.0f).build())
                    .setColorData(ColorParticleData.create(hotA, hotB).setCoefficient(1.6f).setEasing(Easing.EXPO_OUT).build())
                    .setSpinData(SpinParticleData.create(0.8f, 0.0f).setEasing(Easing.QUARTIC_OUT).build())
                    .setLifetime(10 + (int)(hash(seed ^ 0x444, i) * 6))
                    .addMotion((hash(seed ^ 0x555, i) - 0.5) * 0.2, 0.05, (hash(seed ^ 0x666, i) - 0.5) * 0.2)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);
        }
    }

    private static void spawnImplosionSwirl(net.minecraft.client.world.ClientWorld world,
                                            double cx, double cy, double cz,
                                            float radius, int seed) {
        // Small inward spirals to hint at vacuum before main shock
        int arms = 5;
        int steps = 28;
        Color c1 = new Color(170, 10, 220), c2 = new Color(255, 60, 245);
        for (int a = 0; a < arms; a++) {
            double phase = (a / (double)arms) * Math.PI * 2.0;
            for (int s = 0; s < steps; s++) {
                double t = s / (double)steps;
                double r = 2.5 * (1.0 - t);
                double ang = phase + t * 6.0; // spiral
                double x = cx + Math.cos(ang) * r;
                double z = cz + Math.sin(ang) * r;
                double y = cy + 0.05;

                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.2f, 0.0f).build())
                        .setTransparencyData(GenericParticleData.create(0.6f, 0.0f).build())
                        .setColorData(ColorParticleData.create(c1, c2).setEasing(Easing.SINE_IN_OUT).build())
                        .setSpinData(SpinParticleData.create(0.3f, 0.0f).build())
                        .setLifetime(12)
                        .addMotion((cx - x) * 0.05, 0.01, (cz - z) * 0.05)
                        .enableNoClip().setForceSpawn(true).setShouldCull(false)
                        .spawn(world, x, y, z);
            }
        }
    }

    private static void spawnDustWall(net.minecraft.client.world.ClientWorld world,
                                      double cx, double cy, double cz,
                                      float radius, int seed) {
        int segments = Math.max(64, Math.min(220, (int)(radius * 3.2f)));
        Color dA = new Color(65, 36, 68), dB = new Color(28, 18, 36);
        for (int i = 0; i < segments; i++) {
            double a = (i / (double)segments) * Math.PI * 2.0 + (hash(seed ^ 0xD00D, i) - 0.5) * 0.06;
            double r = radius * (0.98 + (hash(seed ^ 0xD33D, i) - 0.5) * 0.06);
            double x = cx + Math.cos(a) * r;
            double z = cz + Math.sin(a) * r;
            // Slightly above ground
            double y = cy + 0.05 + (hash(seed ^ 0xD55D, i) * 0.15);

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.35f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.5f, 0.0f).build())
                    .setColorData(ColorParticleData.create(dA, dB).setEasing(Easing.QUAD_OUT).build())
                    .setLifetime(24 + (int)(hash(seed ^ 0xD77D, i) * 16))
                    .addMotion(Math.cos(a) * 0.08, 0.01, Math.sin(a) * 0.08)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);
        }
    }

    private static void spawnMushroomColumn(net.minecraft.client.world.ClientWorld world,
                                            double cx, double cy, double cz,
                                            float radius, int seed) {
        // Column: rising wisps with slight roll
        int col = 80;
        Color cLow = new Color(120, 20, 150), cHigh = new Color(60, 10, 90);
        double rise = Math.min(18.0, 6.0 + radius * 0.18);
        for (int i = 0; i < col; i++) {
            double t = hash(seed ^ 0xC011, i);
            double h = t * rise;
            double swirl = (hash(seed ^ 0xC0DE, i) - 0.5) * 0.6 + h * 0.08; // roll with height
            double r = 1.2 + (hash(seed ^ 0xC0Ff, i) * 0.8);
            double x = cx + Math.cos(swirl) * r;
            double z = cz + Math.sin(swirl) * r;
            double y = cy + 0.4 + h;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.28f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.55f, 0.0f).build())
                    .setColorData(ColorParticleData.create(cLow, cHigh).setEasing(Easing.SINE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 0.5f).setEasing(Easing.QUAD_OUT).build())
                    .setLifetime(26 + (int)(t * 22))
                    .addMotion((hash(seed ^ 0xC123, i) - 0.5) * 0.02, 0.06 + t * 0.02, (hash(seed ^ 0xC456, i) - 0.5) * 0.02)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);
        }

        // Cap: wide ring at top
        int cap = 60;
        double topY = cy + rise + 0.8;
        double capR = 2.0 + Math.min(6.0, radius * 0.18);
        for (int i = 0; i < cap; i++) {
            double a = (i / (double)cap) * Math.PI * 2.0 + hash(seed ^ 0xCAFE5, i) * 0.1;
            double x = cx + Math.cos(a) * capR;
            double z = cz + Math.sin(a) * capR;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.35f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.6f, 0.0f).build())
                    .setColorData(ColorParticleData.create(cHigh, cLow).setEasing(Easing.SINE_IN_OUT).build())
                    .setLifetime(26 + (int)(hash(seed ^ 0xC4D2, i) * 18))
                    .addMotion(Math.cos(a) * 0.04, 0.02, Math.sin(a) * 0.04)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, topY, z);
        }
    }

    private static void spawnPressureBursts(net.minecraft.client.world.ClientWorld world,
                                            double cx, double cy, double cz,
                                            float radius, int seed) {
        int bursts = 8;
        Color hot = new Color(255, 80, 120), cool = new Color(255, 60, 245);
        for (int i = 0; i < bursts; i++) {
            double a = hash(seed ^ 0xF00D, i) * Math.PI * 2.0;
            double r = radius * (0.85 + hash(seed ^ 0xFEED, i) * 0.25);
            double x = cx + Math.cos(a) * r;
            double z = cz + Math.sin(a) * r;
            double y = cy + 0.3 + hash(seed ^ 0xFADE, i) * 0.6;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.9f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(1.2f, 0.0f).build())
                    .setColorData(ColorParticleData.create(hot, cool).setCoefficient(1.3f).setEasing(Easing.CIRC_OUT).build())
                    .setSpinData(SpinParticleData.create(0.2f, 1.0f).setEasing(Easing.QUAD_OUT).build())
                    .setLifetime(12)
                    .addMotion((hash(seed ^ 0x1111, i) - 0.5) * 0.12, 0.05, (hash(seed ^ 0x2222, i) - 0.5) * 0.12)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);
        }
    }

    private static void spawnLightningRimArcs(net.minecraft.client.world.ClientWorld world,
                                              double cx, double cy, double cz,
                                              float radius, int seed, int arcCount) {
        Color cA = new Color(240, 120, 255), cB = new Color(180, 40, 255);
        for (int k = 0; k < arcCount; k++) {
            double a = hash(seed ^ (0xAC00 + k), k) * Math.PI * 2.0;
            double x0 = cx + Math.cos(a) * (radius * 0.95);
            double z0 = cz + Math.sin(a) * (radius * 0.95);
            double y0 = cy + 0.3 + hash(seed ^ (0xAC10 + k), k) * 1.2;

            // Jittered polyline outward
            int segs = 7;
            double len = 2.2;
            double px = x0, py = y0, pz = z0;
            for (int s = 0; s < segs; s++) {
                double t = s / (double)segs;
                double x1 = cx + Math.cos(a) * (radius + t * len) + (hash(seed ^ (k*31 + s), s) - 0.5) * 0.6;
                double y1 = py + (hash(seed ^ (k*53 + s), s) - 0.5) * 0.8;
                double z1 = cz + Math.sin(a) * (radius + t * len) + (hash(seed ^ (k*71 + s), s) - 0.5) * 0.6;

                // Particle at segment point
                WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.16f, 0.05f).build())
                        .setTransparencyData(GenericParticleData.create(1.0f, 0.0f).build())
                        .setColorData(ColorParticleData.create(cA, cB).setCoefficient(1.4f).setEasing(Easing.QUAD_OUT).build())
                        .setSpinData(SpinParticleData.create(0.9f, 1.2f).build())
                        .setLifetime(8)
                        .addMotion(0, 0, 0)
                        .enableNoClip().setForceSpawn(true).setShouldCull(false)
                        .spawn(world, x1, y1, z1);

                px = x1; py = y1; pz = z1;
            }
        }
    }

    private static void spawnAshFallout(net.minecraft.client.world.ClientWorld world,
                                        double cx, double cy, double cz,
                                        float radius, int seed) {
        int count = 140;
        Color ashA = new Color(60, 30, 70), ashB = new Color(30, 18, 40);
        for (int i = 0; i < count; i++) {
            double a = hash(seed ^ 0xA5E0, i) * Math.PI * 2.0;
            double r = radius * (0.9 + hash(seed ^ 0xA5E1, i) * 0.25);
            double x = cx + Math.cos(a) * r;
            double z = cz + Math.sin(a) * r;
            double y = cy + 1.2 + hash(seed ^ 0xA5E2, i) * 3.5;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.25f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.45f, 0.0f).build())
                    .setColorData(ColorParticleData.create(ashA, ashB).setEasing(Easing.SINE_IN_OUT).build())
                    .setLifetime(34 + (int)(hash(seed ^ 0xA5E3, i) * 28))
                    .addMotion((hash(seed ^ 0xA5E4, i) - 0.5) * 0.02, -0.025 - hash(seed ^ 0xA5E5, i) * 0.02, (hash(seed ^ 0xA5E6, i) - 0.5) * 0.02)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);

            if (hash(seed ^ 0xA5E7, i) < 0.14) {
                WorldParticleBuilder.create(LodestoneParticleRegistry.STAR_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.08f, 0.24f, 0.0f).build())
                        .setTransparencyData(GenericParticleData.create(1.0f, 0.0f).build())
                        .setColorData(ColorParticleData.create(new Color(255, 160, 120), new Color(255, 80, 80)).setEasing(Easing.QUAD_OUT).build())
                        .setLifetime(18)
                        .addMotion((hash(seed ^ 0xA5E8, i) - 0.5) * 0.03, 0.02, (hash(seed ^ 0xA5E9, i) - 0.5) * 0.03)
                        .enableNoClip().setForceSpawn(true).setShouldCull(false)
                        .spawn(world, x, y, z);
            }
        }
    }

    private static void spawnRuneRing(net.minecraft.client.world.ClientWorld world,
                                      double cx, double cy, double cz,
                                      double r, int seed) {
        int glyphs = 16;
        Color ca = new Color(220, 60, 255), cb = new Color(120, 30, 160);
        for (int i = 0; i < glyphs; i++) {
            double a = (i / (double)glyphs) * Math.PI * 2.0 + hash(seed ^ 0xBADC0DE, i) * 0.08;
            double x = cx + Math.cos(a) * r;
            double z = cz + Math.sin(a) * r;
            double y = cy + 0.02;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.7f, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(1.1f, 0.0f).build())
                    .setColorData(ColorParticleData.create(ca, cb).setCoefficient(1.3f).setEasing(Easing.QUAD_OUT).build())
                    .setSpinData(SpinParticleData.create(0.0f, 1.2f).setEasing(Easing.CUBIC_OUT).build())
                    .setLifetime(14)
                    .addMotion(0, 0.01, 0)
                    .enableNoClip().setForceSpawn(true).setShouldCull(false)
                    .spawn(world, x, y, z);
        }
    }


    // Simple deterministic [0,1) hash on (seed,i)
    private static double hash(int seed, int i) {
        long h = (seed * 0x9E3779B97F4A7C15L) ^ (long)i * 0xBF58476D1CE4E5B9L;
        h ^= (h >>> 30); h *= 0xBF58476D1CE4E5B9L;
        h ^= (h >>> 27); h *= 0x94D049BB133111EBL;
        h ^= (h >>> 31);
        return ((h >>> 11) & ((1L << 53) - 1)) / (double)(1L << 53);
    }
}
