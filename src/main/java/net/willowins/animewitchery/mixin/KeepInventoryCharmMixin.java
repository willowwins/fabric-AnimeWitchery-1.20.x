package net.willowins.animewitchery.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.willowins.animewitchery.item.custom.KeepInventoryCharmItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class KeepInventoryCharmMixin {
    
    @Shadow public abstract boolean isSpectator();
    
    // Static map to track players and their original gamerule state
    private static final Map<UUID, Boolean> playersWithCharm = new HashMap<>();
    
    /**
     * Check for Keep Inventory Charm before death and enable keepInventory gamerule.
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void checkForCharmAndEnableRule(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient) return;
        
        // Check for Keep Inventory Charm in player inventory
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof KeepInventoryCharmItem) {
                // Store the current gamerule state
                boolean currentState = player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY);
                playersWithCharm.put(player.getUuid(), currentState);
                
                // Enable keepInventory
                player.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(true, player.getServer());
                
                // Consume the charm
                stack.decrement(1);
                return;
            }
        }
    }
    
    /**
     * Restore the gamerule after death is fully processed.
     */
    @Inject(method = "onDeath", at = @At("RETURN"))
    private void restoreGameruleAfterDeath(DamageSource damageSource, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (player.getWorld().isClient) return;
        
        if (playersWithCharm.containsKey(player.getUuid())) {
            // Restore the original gamerule state
            boolean originalState = playersWithCharm.get(player.getUuid());
            player.getWorld().getGameRules().get(GameRules.KEEP_INVENTORY).set(originalState, player.getServer());
            
            // Remove from map
            playersWithCharm.remove(player.getUuid());
        }
    }
}

