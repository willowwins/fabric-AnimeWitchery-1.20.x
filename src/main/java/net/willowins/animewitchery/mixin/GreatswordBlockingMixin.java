package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.custom.ResonantGreatSwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class GreatswordBlockingMixin {

    @ModifyVariable(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V"), argsOnly = true)
    private float reduceBlockedDamageAndAddCharge(float amount, DamageSource source) {
        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (entity instanceof PlayerEntity player) {
            ItemStack activeStack = player.getActiveItem();
            
            // Check if actively blocking with the resonant greatsword
            if (player.isBlocking() && activeStack.getItem() instanceof ResonantGreatSwordItem) {
                // Calculate reduced damage (75% reduction means player takes 25% of damage)
                float reduction = ResonantGreatSwordItem.getBlockDamageReduction();
                float blockedAmount = amount * reduction;
                float reducedAmount = amount * (1 - reduction);
                
                // Add charge based on how much damage was blocked
                ResonantGreatSwordItem.addChargeFromBlockedDamage(activeStack, blockedAmount);
                
                // Return the reduced damage amount
                return reducedAmount;
            }
        }
        
        return amount;
    }
}


