package net.willowins.animewitchery.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.willowins.animewitchery.item.custom.ObeliskCompassItem;

@Environment(EnvType.CLIENT)
public class ObeliskCompassHud implements ClientModInitializer {

    // Cached target info for smooth HUD transitions
    private static BlockPos cachedTarget = null;
    private static float cachedAngle = 0f;
    private static double cachedDistance = 0d;
    private static boolean hasTarget = false;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(ObeliskCompassHud::onHudRender);
    }

    private static void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        ItemStack stack = findCompassInHotbar(client);
        if (stack == null || !(stack.getItem() instanceof ObeliskCompassItem)) {
            hasTarget = false;
            return;
        }

        // 🔍 Always refresh data from compass NBT each tick
        if (stack.hasNbt() && stack.getNbt().getBoolean("HasTarget")) {
            hasTarget = true;
            cachedTarget = new BlockPos(
                    stack.getNbt().getInt("ObeliskX"),
                    stack.getNbt().getInt("ObeliskY"),
                    stack.getNbt().getInt("ObeliskZ")
            );
            cachedAngle = stack.getNbt().getFloat("ObeliskAngle");

            if (client.player != null && cachedTarget != null) {
                cachedDistance = Math.sqrt(client.player.getBlockPos().getSquaredDistance(
                        cachedTarget.getX() + 0.5,
                        cachedTarget.getY() + 0.5,
                        cachedTarget.getZ() + 0.5
                ));
            }
        } else {
            hasTarget = false;
            cachedTarget = null;
        }

        // If no encoded data → hide HUD
        if (!hasTarget || cachedTarget == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int xCenter = screenWidth / 2;
        int y = screenHeight - 58; // just above hotbar

        RenderSystem.enableBlend();

        // Dynamic color shifts
        int angleColor = angleAccuracyColor(cachedAngle);
        int distColor = distanceColor(cachedDistance);

        String angleText = String.format("Angle: %.1f°", displayAngle(cachedAngle));
        String distText = String.format("Distance: %.1fm", cachedDistance);

        context.drawTextWithShadow(client.textRenderer, Text.literal(angleText), xCenter - 40, y, angleColor);
        context.drawTextWithShadow(client.textRenderer, Text.literal(distText), xCenter - 40, y + 10, distColor);

        RenderSystem.disableBlend();
    }

    /** Finds the compass in the player's hotbar */
    private static ItemStack findCompassInHotbar(MinecraftClient client) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ObeliskCompassItem) return stack;
        }
        return null;
    }

    /** Normalize displayed angle — numbers above 180 decrease back toward 0 for intuitive feedback */
    private static float displayAngle(float angle) {
        float normalized = (angle % 360 + 360) % 360;
        if (normalized > 180) normalized = 360 - normalized;
        return normalized;
    }

    /** Gradient from blue→aqua depending on heading accuracy */
    private static int angleAccuracyColor(float angle) {
        float normalized = (angle % 360 + 360) % 360;
        float deviation = Math.min(Math.abs(normalized) / 180f, 1f);
        int blue = (int) (255 * (1f - deviation));
        int green = (int) (255 * deviation);
        return (0xFF << 24) | (blue << 8) | green;
    }

    /** Gradient from green (close) → red (far) */
    private static int distanceColor(double distance) {
        double clamped = Math.min(distance / 2000.0, 1.0);
        int green = (int) (255 * (1.0 - clamped));
        int red = (int) (255 * clamped);
        return (0xFF << 24) | (red << 16) | (green << 8);
    }
}
