package net.willowins.animewitchery.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class KeepInventoryCharmMixin {
    
    @Unique
    private boolean animewitchery$hasCharm = false;
    
    /**
     * Check for Keep Inventory Charm before death and temporarily enable keepInventory gamerule.
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void checkForCharmOnDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient) return;
        
        // Check for Keep Inventory Charm in player inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof KeepInventoryCharmItem) {
                // Mark that we have the charm
                animewitchery$hasCharm = true;
                
                // Temporarily enable keepInventory gamerule
                player.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true, player.getServer());
                
                // Consume the charm
                stack.decrement(1);
                return;
            }
        }
    }
    
    /**
     * Restore the keepInventory gamerule after death processing.
     */
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void restoreGameruleAfterDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient) return;
        
        if (animewitchery$hasCharm) {
            // Restore keepInventory gamerule to false
            player.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(false, player.getServer());
            animewitchery$hasCharm = false;
        }
    }
}

