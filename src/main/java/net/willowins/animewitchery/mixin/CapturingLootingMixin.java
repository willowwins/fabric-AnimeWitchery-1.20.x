package net.willowins.animewitchery.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class CapturingLootingMixin {
    
    /**
     * Add Looting III bonus when Capturing enchantment is present.
     * This makes Capturing also provide better loot drops.
     */
    @Inject(method = "getLooting", at = @At("RETURN"), cancellable = true)
    private static void addCapturingLooting(LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity == null) return;
        
        // Check if the entity has Capturing enchantment on their main hand
        ItemStack mainHand = entity.getMainHandStack();
        int capturingLevel = EnchantmentHelper.getLevel(ModEnchantments.CAPTURING, mainHand);
        
        if (capturingLevel > 0) {
            // Add Looting III (level 3) to the existing looting level
            int currentLooting = cir.getReturnValue();
            cir.setReturnValue(currentLooting + 3);
        }
    }
}

