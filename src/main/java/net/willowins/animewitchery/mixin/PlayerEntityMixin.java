package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.item.custom.RailgunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

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
