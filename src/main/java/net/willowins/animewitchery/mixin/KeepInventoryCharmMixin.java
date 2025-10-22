package net.willowins.animewitchery.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public class KeepInventoryCharmMixin {
    
    // Static set to track players who should keep inventory (persists across instance changes)
    private static final Set<UUID> playersWithCharm = new HashSet<>();
    
    /**
     * Check for Keep Inventory Charm before death.
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void checkForCharm(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient) return;
        
        // Check for Keep Inventory Charm in player inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof KeepInventoryCharmItem) {
                // Track this player
                playersWithCharm.add(player.getUuid());
                // Consume the charm
                stack.decrement(1);
                return;
            }
        }
    }
    
    /**
     * Cancel inventory drop if charm was found.
     */
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void preventInventoryDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (playersWithCharm.contains(player.getUuid())) {
            ci.cancel();
            // Remove from set after use
            playersWithCharm.remove(player.getUuid());
        }
    }
}

