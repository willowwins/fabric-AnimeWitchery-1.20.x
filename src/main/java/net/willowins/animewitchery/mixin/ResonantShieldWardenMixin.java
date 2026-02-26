package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.entity.custom.ResonantShieldEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(LivingEntity.class)
public class ResonantShieldWardenMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void blockWardenBeam(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player && player.isUsingItem()) {
            ItemStack activeItem = player.getActiveItem();

            if (activeItem.getItem() == ModItems.RESONANT_SHIELD) {
                if (source.getName().equals("sonic_boom")) {
                    // Try to find the shield entity to feed it the damage
                    List<ResonantShieldEntity> shields = player.getWorld().getEntitiesByClass(
                            ResonantShieldEntity.class,
                            player.getBoundingBox().expand(3.0),
                            s -> s.getOwner() == player);

                    if (!shields.isEmpty()) {
                        ResonantShieldEntity shield = shields.get(0);
                        shield.damage(source, amount); // Feed the shield so it grows

                        // Play blocking sound
                        player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                net.minecraft.sound.SoundEvents.ITEM_SHIELD_BLOCK,
                                net.minecraft.sound.SoundCategory.PLAYERS, 1.0f,
                                0.8f + player.getWorld().random.nextFloat() * 0.4f);
                    }

                    cir.setReturnValue(false); // Cancel player damage
                }
            }
        }
    }
}
