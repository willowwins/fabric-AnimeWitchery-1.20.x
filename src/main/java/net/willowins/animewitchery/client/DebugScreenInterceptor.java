package net.willowins.animewitchery.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Set;

/**
 * Fully disables the F3 debug screen in specific dimensions
 * without any flicker or partial frame render.
 */
public class DebugScreenInterceptor implements ClientModInitializer {

    // Dimensions where debug info is forbidden
    private static final Set<Identifier> DISABLED_DIMENSIONS = Set.of(
            new Identifier("animewitchery:paradiselostdim")
    );

    // GLFW key constant for F3
    private static final int F3_KEY = InputUtil.GLFW_KEY_F3;

    private boolean warnedThisPress = false;

    @Override
    public void onInitializeClient() {

        // --- Prevent F3 from toggling debug info ---
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null || client.world == null || client.player == null) return;

            Identifier dimId = client.world.getRegistryKey().getValue();
            boolean blocked = DISABLED_DIMENSIONS.contains(dimId);
            if (!blocked) return;

            long window = client.getWindow().getHandle();
            boolean f3Pressed = InputUtil.isKeyPressed(window, F3_KEY);

            if (f3Pressed) {
                // Disable all debug info immediately
                client.options.debugEnabled = false;
                client.options.getReducedDebugInfo().setValue(true);

                if (!warnedThisPress) {
                    client.player.sendMessage(
                            Text.literal("§cThe veil of this realm hides all knowledge (F3 disabled)."),
                            true
                    );
                    warnedThisPress = true;
                }
            } else {
                warnedThisPress = false;
            }
        });

        // --- Extra safety: block HUD-level debug render ---
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.world == null) return;

            if (DISABLED_DIMENSIONS.contains(client.world.getRegistryKey().getValue())) {
                if (client.options.debugEnabled) {
                    client.options.debugEnabled = false;
                    client.options.getReducedDebugInfo().setValue(true);
                }
            }
        });
    }
}
