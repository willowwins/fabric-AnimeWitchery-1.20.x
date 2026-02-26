package net.willowins.animewitchery.mixin;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandFuelMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean allowCustomFuel(ItemStack instance, Item item) {
        if (item == Items.BLAZE_POWDER) {
            return instance.isOf(Items.BLAZE_POWDER) || instance.isOf(ModItems.TORCH_FLOWER_ESSENCE);
        }
        return instance.isOf(item);
    }

    @Redirect(method = "isValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean allowCustomFuelInIsValid(ItemStack instance, Item item) {
        if (item == Items.BLAZE_POWDER) {
            return instance.isOf(Items.BLAZE_POWDER) || instance.isOf(ModItems.TORCH_FLOWER_ESSENCE);
        }
        return instance.isOf(item);
    }
}
