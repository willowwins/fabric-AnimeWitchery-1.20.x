package net.willowins.animewitchery.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.ScytheItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class ScytheSweepingMixin {

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")
    )
    private Item scytheTriggersSweep(ItemStack instance) {
        Item item = instance.getItem();
        if (item instanceof ScytheItem) {
            return net.minecraft.item.Items.WOODEN_SWORD;
        }
        return item;
    }

    @Redirect(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttackCooldownProgress(F)F")
    )
    private float scytheAlwaysFullCooldown(PlayerEntity player, float baseTime) {
        if (player.getMainHandStack().getItem() instanceof ScytheItem) {
            return 1.0F;
        }
        return player.getAttackCooldownProgress(baseTime);
    }

    @Redirect(
            method = "attack",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;fallDistance:F",
                    opcode = Opcodes.GETFIELD
            )
    )
    private float scytheDisablesCriticalHits(PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof ScytheItem) {
            return 0.0F;
        }
        return player.fallDistance;
    }

    @ModifyVariable(
            method = "attack",
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 3
    )
    private float wideSweepRange(float original) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Item item = player.getMainHandStack().getItem();
        if (item instanceof ScytheItem scythe) {
            return original + scythe.getRadius();
        }
        return original;
    }
}
