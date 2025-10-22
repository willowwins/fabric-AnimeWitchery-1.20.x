package net.willowins.animewitchery.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class KeepInventoryCharmMixin {
    
    @Unique
    private boolean animewitchery$shouldKeepInventory = false;
    
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
                animewitchery$shouldKeepInventory = true;
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
        if (animewitchery$shouldKeepInventory) {
            ci.cancel();
            animewitchery$shouldKeepInventory = false;
        }
    }
}

