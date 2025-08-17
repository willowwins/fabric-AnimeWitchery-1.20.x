package net.willowins.animewitchery.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.effect.VoidBoundEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    // Block attacking entities
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (!VoidBoundEffect.canInteractWithWorld(player)) {
            ci.cancel(); // Can't attack in void phase
        }
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void applyCooldownToHeldItems(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.hasStatusEffect(ModEffect.BOUND)) {
            if (!player.getWorld().isClient) {
                // Apply to both main and offhand
                ItemStack mainHand = player.getMainHandStack();
                ItemStack offHand = player.getOffHandStack();

                if (!mainHand.isEmpty()) {
                    Item mainItem = mainHand.getItem();
                    player.getItemCooldownManager().set(mainItem, 20); // 5 seconds
                }

                if (!offHand.isEmpty()) {
                    Item offItem = offHand.getItem();
                    player.getItemCooldownManager().set(offItem, 20);
                }

            }
        }
    }
}