package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class GreatswordWardenBeamMixin {
    
    /**
     * Block Warden sonic boom attacks when using Resonant Greatsword and give full charge.
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void blockWardenBeam(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        // Check if entity is a player blocking with Resonant Greatsword
        if (entity instanceof PlayerEntity player && player.isUsingItem()) {
            ItemStack activeItem = player.getActiveItem();
            
            if (activeItem.getItem() instanceof ResonantGreatSwordItem) {
                // Check if the damage source is a Warden's sonic boom
                if (source.getName().equals("sonic_boom")) {
                    // Block the attack completely
                    cir.setReturnValue(false);
                    
                    // Grant full charge
                    float maxCharge = ResonantGreatSwordItem.getMaxCharge();
                    activeItem.getOrCreateNbt().putFloat("charge", maxCharge);
                    
                    // Optionally play a sound or spawn particles here
                }
            }
        }
    }
}

