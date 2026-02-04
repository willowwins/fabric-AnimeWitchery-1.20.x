package net.willowins.animewitchery.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.mana.ManaHelper;
import net.willowins.animewitchery.util.MidnightWandState;

public class ManaHudOverlay implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((DrawContext drawContext, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null || client.options.hudHidden)
                return;

            TextRenderer font = client.textRenderer;
            int x = 10;
            int y = client.getWindow().getScaledHeight() - 20;

            // Early return if no mana storage - rest of HUD only shows with mana items
            if (!ManaHelper.hasManaStorageItem(player))
                return;

            // === Entity Radar (around crosshair) ===
            renderEntityRadar(drawContext, client, player);

            int totalMana = ManaHelper.getTotalMana(player);
            int maxMana = ManaHelper.getTotalMaxMana(player);

            float manaRatio = Math.max(0f, Math.min(1f, totalMana / (float) maxMana));

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
            int endColor = 0xFF440044; // dark purple
            int manaColor = interpolateColor(startColor, endColor, manaRatio);
            drawContext.fill(barX, barY, barX + filled, barY + barHeight, manaColor);

            // === Totem Symbols (horizontal bars stacked vertically above mana bar) ===
            int symbolY = barY - 12; // Start above the mana bar

            // Display Keep Inventory Charm symbols (👁 - purple eye) - horizontal bar
            int charmCount = countItemInInventory(player, ModItems.KEEP_INVENTORY_CHARM);
            if (charmCount > 0) {
                int symbolX = barX;
                for (int i = 0; i < charmCount; i++) {
                    drawContext.drawTextWithShadow(font, "👁", symbolX, symbolY, 0xFFAA00FF);
                    symbolX += 10; // Next symbol to the right
                }
                symbolY -= 10; // Move to next row
            }

            // Display Mod Totem symbols (⚔ - crossed swords) - horizontal bar
            int modTotemCount = countItemInInventory(player, ModItems.MOD_TOTEM);
            if (modTotemCount > 0) {
                int symbolX = barX;
                for (int i = 0; i < modTotemCount; i++) {
                    drawContext.drawTextWithShadow(font, "⚔", symbolX, symbolY, 0xFFFF4444);
                    symbolX += 10; // Next symbol to the right
                }
                symbolY -= 10; // Move to next row
            }

            // Display vanilla Totem of Undying symbols (⛨ - gold shield) - horizontal bar
            int vanillaTotemCount = countItemInInventory(player, Items.TOTEM_OF_UNDYING);
            if (vanillaTotemCount > 0) {
                int symbolX = barX;
                for (int i = 0; i < vanillaTotemCount; i++) {
                    drawContext.drawTextWithShadow(font, "⛨", symbolX, symbolY, 0xFFFFD700);
                    symbolX += 10; // Next symbol to the right
                }
                symbolY -= 10; // Move to next row
            }

            // === Conditional Speed Display (above all totems) ===
            if (player.isFallFlying()) {
                Vec3d vel = player.getVelocity();
                double rawSpeed = vel.length() * 20.0; // blocks/sec

                // Filter out jitter from micro-motion (anything <0.5 m/s is zero)
                double speed = (rawSpeed < 0.5) ? 0.0 : rawSpeed;

                String speedText = String.format("Speed: %.2f m/s", speed);
                int speedColor = interpolateColor(0xFF00AAFF, 0xFFFFFFFF, (float) Math.min(speed / 60.0, 1.0));

                // Position speed indicator above all totem bars
                drawContext.drawTextWithShadow(font, speedText, x, symbolY - 2, speedColor);
            }

            // === Eternal Winter Countdown Timer (Top Right) ===
            if (client.world != null && client.getServer() != null) {
                ServerWorld serverWorld = client.getServer().getOverworld();
                if (serverWorld != null) {
                    MidnightWandState state = MidnightWandState.getOrCreate(serverWorld);

                    // Always show timer if there's an end time set (even if not currently active)
                    if (state.getMidnightEndTime() > 0) {
                        long remainingTicks = state.getMidnightEndTime() - client.world.getTime();

                        // Convert ticks to minutes and seconds
                        long totalSeconds = Math.max(0, remainingTicks / 20);
                        long minutes = totalSeconds / 60;
                        long seconds = totalSeconds % 60;

                        String timerText;
                        int timerColor;

                        if (state.isMidnightActive(client.world.getTime())) {
                            // Active winter - show countdown
                            timerText = String.format("❄ %d:%02d", minutes, seconds);
                            timerColor = 0xFF00FFFF; // Cyan
                        } else {
                            // Winter ended - show "Dawn has come"
                            timerText = "❄ Dawn has come";
                            timerColor = 0xFFFFD700; // Golden yellow
                        }

                        // Position in top right corner
                        int timerX = client.getWindow().getScaledWidth() - font.getWidth(timerText) - 10;
                        int timerY = 10;

                        drawContext.drawTextWithShadow(font, timerText, timerX, timerY, timerColor);
                    }
                }
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

    /** Count how many of a specific item the player has in their inventory */
    private static int countItemInInventory(PlayerEntity player, net.minecraft.item.Item item) {
        int count = 0;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /** Renders directional indicators around the crosshair */
    private static void renderEntityRadar(DrawContext drawContext, MinecraftClient client, PlayerEntity player) {
        if (client.world == null)
            return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Settings
        int innerRadius = 12; // Distance from crosshair to start of arc
        int arcLength = 5; // Length of the arc
        double detectionRange = 15.0; // Detection range in blocks

        // Get player position and rotation
        Vec3d playerPos = player.getPos();
        float playerYaw = player.getYaw();
        long currentTime = client.world.getTime();

        // Clean up expired attacker marks
        net.willowins.animewitchery.client.AttackerTracker.cleanup(currentTime);

        // Iterate through all entities in the world
        for (Entity entity : client.world.getEntities()) {
            if (entity == player)
                continue; // Skip the player
            if (entity instanceof net.minecraft.entity.ItemEntity)
                continue; // Skip dropped items

            // Check if entity is marked as an attacker (shows regardless of range)
            boolean isMarkedAttacker = net.willowins.animewitchery.client.AttackerTracker.isMarked(entity.getUuid(),
                    currentTime);

            // Skip far entities unless they're marked attackers
            if (!isMarkedAttacker && entity.squaredDistanceTo(player) > detectionRange * detectionRange)
                continue;

            // Calculate relative position
            Vec3d entityPos = entity.getPos();
            Vec3d relativePos = entityPos.subtract(playerPos);
            double distance = relativePos.length();

            // Calculate angle to entity (relative to player's facing direction)
            double angleToEntity = Math.atan2(relativePos.z, relativePos.x) - Math.toRadians(playerYaw + 90);

            // Normalize distance to 0-1 range for opacity (capped at detection range for
            // normal entities)
            double normalizedDistance = Math.min(distance / detectionRange, 1.0);

            // Determine entity color based on type
            int baseColor;
            if (isMarkedAttacker) {
                // Marked attackers show in bright orange/red regardless of type
                baseColor = 0xFF6600; // Bright orange-red for marked attackers
            } else if (entity instanceof PlayerEntity) {
                baseColor = 0xAA00FF; // Purple for players
            } else if (entity instanceof net.minecraft.entity.mob.SlimeEntity ||
                    entity instanceof net.minecraft.entity.mob.MagmaCubeEntity) {
                baseColor = 0xFF0000; // Red for slimes and magma cubes
            } else if (entity instanceof HostileEntity) {
                baseColor = 0xFF0000; // Red for hostile mobs
            } else if (entity instanceof PassiveEntity) {
                // Check if tamed
                if (entity instanceof net.minecraft.entity.passive.TameableEntity tameable && tameable.isTamed()) {
                    baseColor = 0x0088FF; // Blue for tamed mobs
                } else {
                    baseColor = 0x00FF00; // Green for passive mobs
                }
            } else {
                baseColor = 0xFFFF00; // Yellow for other entities
            }

            // Calculate opacity based on distance (closer = more opaque)
            // Marked attackers are always fully opaque
            int opacity;
            if (isMarkedAttacker) {
                opacity = 255; // Full opacity for marked attackers
            } else {
                opacity = (int) (255 * (1.0 - normalizedDistance * 0.5)); // 50-100% opacity for normal entities
            }
            int color = (opacity << 24) | baseColor;

            // Calculate arc width based on distance (closer = wider)
            // Marked attackers always show wide arcs
            int arcWidth;
            if (isMarkedAttacker) {
                arcWidth = 60; // Maximum width for marked attackers
            } else {
                // Very close (0 blocks) = 60 degrees, far (15 blocks) = 20 degrees
                arcWidth = (int) (60 - (normalizedDistance * 40));
            }

            // Draw arc segment in the direction of the entity
            drawDirectionalArc(drawContext, centerX, centerY, innerRadius, arcLength, angleToEntity, color, arcWidth);
        }
    }

    /** Draws an arc segment pointing towards a direction */
    private static void drawDirectionalArc(DrawContext drawContext, int centerX, int centerY, int innerRadius,
            int arcLength, double angle, int color, int arcWidthDegrees) {
        int segments = 15;

        // Draw a single clean arc at the specified radius
        int radius = innerRadius + arcLength / 2; // Draw at the middle of the arc length

        for (int i = 0; i <= segments; i++) {
            double a = angle + Math.toRadians(-arcWidthDegrees / 2.0 + (double) i / segments * arcWidthDegrees);

            // Draw single pixel on the arc circle
            // Negate Y so that up = forward (angle 0 = up on screen)
            int x = centerX + (int) (Math.sin(a) * radius);
            int y = centerY - (int) (Math.cos(a) * radius);
            drawContext.fill(x, y, x + 1, y + 1, color);
        }
    }
}
