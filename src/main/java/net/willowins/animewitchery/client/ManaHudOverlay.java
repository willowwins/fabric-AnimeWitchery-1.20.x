package net.willowins.animewitchery.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.willowins.animewitchery.item.custom.AlchemicalCatalystItem;
import net.willowins.animewitchery.mana.IManaComponent;
import net.willowins.animewitchery.mana.ModComponents;

public class ManaHudOverlay implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((DrawContext drawContext, float tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) {
                return;
            }

            // check if the player has at least one catalyst
            boolean hasCatalyst = false;
            for (var stack : client.player.getInventory().main) {
                if (stack.getItem() instanceof AlchemicalCatalystItem) {
                    hasCatalyst = true;
                    break;
                }
            }
            if (!hasCatalyst) {
                return;
            }

            // compute mana + stored
            IManaComponent comp = ModComponents.PLAYER_MANA.get(client.player);
            int playerMana = comp.getMana();
            int storedSum = 0;
            for (var stack : client.player.getInventory().main) {
                if (stack.getItem() instanceof AlchemicalCatalystItem) {
                    storedSum += AlchemicalCatalystItem.getStoredMana(stack);
                }
            }
            int totalMana = playerMana + storedSum;

            String text = "Mana: " + totalMana;

            TextRenderer font = client.textRenderer;
            int x = 10;
            int y = client.getWindow().getScaledHeight() - 20;
            int color = 0x3B9EFF;  // mana blue

            // draw with shadow via DrawContext
            drawContext.drawTextWithShadow(font, text, x, y, color);
        });
    }
}
