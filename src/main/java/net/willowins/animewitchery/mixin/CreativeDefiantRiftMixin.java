package net.willowins.animewitchery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.willowins.animewitchery.item.custom.BanHammerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class CreativeDefiantRiftMixin {
    
    @Inject(method = "attack", at = @At("HEAD"))
    private void onPlayerAttack(Entity target, CallbackInfo ci) {
        PlayerEntity attacker = (PlayerEntity) (Object) this;
        
        // Only process on server side
        if (attacker.getWorld().isClient) {
            return;
        }
        
        // Check if attacker has Defiant Rift (BanHammerItem) in offhand
        if (attacker.getOffHandStack().getItem() instanceof BanHammerItem) {
            // Check if target is a player
            if (target instanceof ServerPlayerEntity victim) {
                boolean wasCreative = victim.isCreative();
                boolean wasOp = victim.hasPermissionLevel(2); // 2 = operator level
                
                // Force victim into survival mode if they're in creative
                if (wasCreative) {
                    victim.changeGameMode(GameMode.SURVIVAL);
                }
                
                // Deop the victim if they're an operator
                if (wasOp && victim.getServer() != null) {
                    victim.getServer().getPlayerManager().removeFromOperators(victim.getGameProfile());
                }
                
                // Send messages if anything changed
                if (wasCreative || wasOp) {
                    StringBuilder message = new StringBuilder("The Defiant Rift has stripped your ");
                    if (wasCreative && wasOp) {
                        message.append("creative powers and operator status!");
                    } else if (wasCreative) {
                        message.append("creative powers!");
                    } else {
                        message.append("operator status!");
                    }
                    
                    victim.sendMessage(
                        Text.literal(message.toString()).formatted(Formatting.DARK_RED),
                        false
                    );
                    
                    // Notify attacker
                    if (attacker instanceof ServerPlayerEntity serverAttacker) {
                        StringBuilder attackerMessage = new StringBuilder("The Defiant Rift has removed ");
                        if (wasCreative && wasOp) {
                            attackerMessage.append(victim.getName().getString()).append("'s creative mode and operator status!");
                        } else if (wasCreative) {
                            attackerMessage.append(victim.getName().getString()).append("'s creative mode!");
                        } else {
                            attackerMessage.append(victim.getName().getString()).append("'s operator status!");
                        }
                        
                        serverAttacker.sendMessage(
                            Text.literal(attackerMessage.toString()).formatted(Formatting.GOLD),
                            false
                        );
                    }
                }
            }
        }
    }
}

