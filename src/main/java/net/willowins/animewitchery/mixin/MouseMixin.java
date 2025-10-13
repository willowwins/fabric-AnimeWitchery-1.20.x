package net.willowins.animewitchery.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.SpellbookItem;
import net.willowins.animewitchery.networking.SpellbookPackets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

@Mixin(Mouse.class)
public class MouseMixin {
    
    @Shadow
    @Final
    private MinecraftClient client;
    
    private int scrollCooldown = 0;
    
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (scrollCooldown > 0) {
            scrollCooldown--;
            return;
        }
        
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        
        // Check if player is sneaking and holding a spellbook
        if (!player.isSneaking()) return;
        
        ItemStack mainHand = player.getMainHandStack();
        if (!(mainHand.getItem() instanceof SpellbookItem)) return;
        
        // Send packet to server to cycle configuration
        if (vertical > 0) {
            // Scroll up - next configuration
            ClientPlayNetworking.send(SpellbookPackets.CYCLE_NEXT, PacketByteBufs.empty());
            ci.cancel(); // Prevent changing held item slot
        } else if (vertical < 0) {
            // Scroll down - previous configuration  
            ClientPlayNetworking.send(SpellbookPackets.CYCLE_PREV, PacketByteBufs.empty());
            ci.cancel(); // Prevent changing held item slot
        }
        
        scrollCooldown = 5; // Small cooldown to prevent accidental double scrolls
    }
}

