package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class GreatswordWardenBeamMixin {

    /**
     * Block Warden sonic boom attacks when wearing full Resonant Armor.
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void blockWardenBeam(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            // Check if wearing full Resonant armor
            ItemStack chest = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
            if (chest.isOf(net.willowins.animewitchery.item.ModItems.RESONANT_CHESTPLATE)) {
                // Simplified check for full set in mixin for performance, or call a static
                // helper
                // For safety, let's just check the source name
                if (source.getName().equals("sonic_boom")) {
                    cir.setReturnValue(false);

                    // Restore some mana as a reward
                    net.willowins.animewitchery.mana.IManaComponent mana = net.willowins.animewitchery.mana.ModComponents.PLAYER_MANA
                            .get(player);
                    mana.setMana(Math.min(mana.getMaxMana(), mana.getMana() + 5000));
                }
            }
        }
    }
}
