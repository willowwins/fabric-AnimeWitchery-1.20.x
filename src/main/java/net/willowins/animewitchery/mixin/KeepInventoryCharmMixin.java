package net.willowins.animewitchery.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class KeepInventoryCharmMixin {
    
    /**
     * Prevent inventory drop on death if player has Keep Inventory Charm equipped.
     * Consume the charm after use.
     */
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void preventInventoryDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Check for Keep Inventory Charm in trinket slots
        if (TrinketsApi.getTrinketComponent(player).isPresent()) {
            var trinketComponent = TrinketsApi.getTrinketComponent(player).get();
            
            // Find and consume the charm
            final boolean[] hasCharm = {false};
            trinketComponent.getAllEquipped().forEach(pair -> {
                ItemStack trinketStack = pair.getRight();
                if (trinketStack.getItem() instanceof KeepInventoryCharmItem && !hasCharm[0]) {
                    hasCharm[0] = true;
                    
                    // Consume the charm
                    if (!player.getWorld().isClient) {
                        trinketStack.decrement(1);
                    }
                }
            });
            
            // If charm was found, cancel inventory drop
            if (hasCharm[0]) {
                ci.cancel();
            }
        }
    }
}

