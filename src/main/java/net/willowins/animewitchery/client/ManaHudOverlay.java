package net.willowins.animewitchery.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;
import net.willowins.animewitchery.mana.ManaHelper;

public class ManaHudOverlay implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((DrawContext drawContext, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null || client.options.hudHidden) return;
            if (!ManaHelper.hasManaStorageItem(player)) return;

            IManaComponent comp = ModComponents.PLAYER_MANA.get(player);
            int totalMana = ManaHelper.getTotalMana(player);
            int maxMana = ManaHelper.getTotalMaxMana(player);

            float manaRatio = Math.max(0f, Math.min(1f, totalMana / (float) maxMana));

            TextRenderer font = client.textRenderer;
            int x = 10;
            int y = client.getWindow().getScaledHeight() - 20;

            // === Mana Text ===
            String manaText = "Mana: " + totalMana + " / " + maxMana;
            int color = 0xFF440044;
            drawContext.drawTextWithShadow(font, manaText, x, y, color);

            // === Mana Bar ===
            int barWidth = 100;
            int barHeight = 6;
            int filled = (int) (manaRatio * barWidth);
            int barX = x;
            int barY = y - 10;

            drawContext.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);

            int startColor = 0xFF004400; // dark green
            int endColor   = 0xFF440044; // dark purple
            int manaColor = interpolateColor(startColor, endColor, manaRatio);
            drawContext.fill(barX, barY, barX + filled, barY + barHeight, manaColor);

            // === Conditional Speed Display ===
            if (player.isFallFlying()) {
                Vec3d vel = player.getVelocity();
                double rawSpeed = vel.length() * 20.0; // blocks/sec

                // Filter out jitter from micro-motion (anything <0.5 m/s is zero)
                double speed = (rawSpeed < 0.5) ? 0.0 : rawSpeed;

                String speedText = String.format("Speed: %.2f m/s", speed);
                int speedColor = interpolateColor(0xFF00AAFF, 0xFFFFFFFF, (float)Math.min(speed / 60.0, 1.0));

                drawContext.drawTextWithShadow(font, speedText, x, barY - 12, speedColor);
            }
        });
    }

    /** Linearly interpolates between two ARGB colors */
    private static int interpolateColor(int startColor, int endColor, float t) {
        int a1 = (startColor >> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
