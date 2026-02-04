package net.willowins.animewitchery.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.item.custom.FulfillingTiaraItem;
import net.willowins.animewitchery.item.custom.ResonantTiaraItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class GlobalTotemMixin {

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void tryUseGlobalTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof PlayerEntity player))
            return;
        if (player.getWorld().isClient)
            return;

        // Check for Tiara
        boolean hasTiara = TrinketsApi.getTrinketComponent(player)
                .map(component -> component.isEquipped(stack -> stack.getItem() instanceof FulfillingTiaraItem
                        || stack.getItem() instanceof ResonantTiaraItem))
                .orElse(false);

        if (hasTiara) {
            ItemStack totemStack = ItemStack.EMPTY;

            // Search inventory for ANY totem
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (!stack.isEmpty() && (stack.isOf(Items.TOTEM_OF_UNDYING) || stack.isOf(ModItems.MOD_TOTEM))) {
                    totemStack = stack;
                    break;
                }
            }

            // Note: Hand slots are checked by vanilla logic later, but we want to intercept
            // GLOBAL inventory.
            // If we found one, we use it. This overrides normal behavior (which requires
            // holding it).

            if (!totemStack.isEmpty()) {
                totemStack.decrement(1);

                player.setHealth(1.0F);
                player.clearStatusEffects();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));

                player.getWorld().sendEntityStatus(player, (byte) 35); // Totem animation

                System.out.println(
                        "ANIMEWITCHERY DEBUG: Global Totem Activated! Player: " + player.getName().getString());
                cir.setReturnValue(true);
            }
        }
    }
}
