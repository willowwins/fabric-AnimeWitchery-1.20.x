package net.willowins.animewitchery.client.input;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.networking.ModPackets;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class PlatformKeybindHandler implements ClientModInitializer {

    private static KeyBinding togglePlatformKey;
    private static boolean platformMode = false;

    @Override
    public void onInitializeClient() {
        // --- Register keybinding ---
        togglePlatformKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.animewitchery.toggle_platform",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.animewitchery"
        ));

        // --- Tick handler ---
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (togglePlatformKey.wasPressed()) {
                platformMode = !platformMode;

                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal(
                                    platformMode ? "⬢ Float Platform Activated" : "⬡ Float Platform Deactivated"
                            ),
                            true
                    );
                }
            }

            // --- Send create-platform packet every 5 ticks if active ---
            if (platformMode && client.player != null && client.player.age % 5 == 0) {
                ClientPlayNetworking.send(ModPackets.CREATE_PLATFORM_ID, PacketByteBufs.create());
            }
        });
    }
}
