package net.willowins.animewitchery.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;

/**
 * Handles client-server communication for spellbook actions
 */
public class SpellbookPackets {
    
    // Packet identifiers
    public static final Identifier CYCLE_NEXT = new Identifier(AnimeWitchery.MOD_ID, "spellbook_cycle_next");
    public static final Identifier CYCLE_PREV = new Identifier(AnimeWitchery.MOD_ID, "spellbook_cycle_prev");
    
    /**
     * Register server-side packet receivers
     */
    public static void registerServerReceivers() {
        // Cycle to next configuration (Shift + Scroll Up)
        ServerPlayNetworking.registerGlobalReceiver(CYCLE_NEXT, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                net.minecraft.item.ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookItem) {
                    net.willowins.animewitchery.item.custom.SpellbookItem.cycleConfigurationNext(stack, player);
                }
            });
        });
        
        // Cycle to previous configuration (Shift + Scroll Down)
        ServerPlayNetworking.registerGlobalReceiver(CYCLE_PREV, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                net.minecraft.item.ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof net.willowins.animewitchery.item.custom.SpellbookItem) {
                    net.willowins.animewitchery.item.custom.SpellbookItem.cycleConfigurationPrevious(stack, player);
                }
            });
        });
    }
}
