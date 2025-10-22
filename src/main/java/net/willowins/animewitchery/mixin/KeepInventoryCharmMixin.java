package net.willowins.animewitchery.mixin;

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
     * Prevent inventory drop on death if player has Keep Inventory Charm in inventory.
     * Consume the charm after use.
     */
    @Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
    private void preventInventoryDrop(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Check for Keep Inventory Charm in player inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof KeepInventoryCharmItem) {
                // Consume the charm
                if (!player.getWorld().isClient) {
                    stack.decrement(1);
                }
                
                // Cancel inventory drop
                ci.cancel();
                return;
            }
        }
    }
}

