package net.willowins.animewitchery.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinBrain.class)
public class PiglinGoldTrimMixin {
    
    @Inject(method = "wearsGoldArmor", at = @At("HEAD"), cancellable = true)
    private static void checkGoldTrim(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        // Check each armor piece for gold trim
        for (ItemStack armorStack : entity.getArmorItems()) {
            if (armorStack.getItem() instanceof ArmorItem && armorStack.hasNbt()) {
                NbtCompound nbt = armorStack.getNbt();
                if (nbt != null && nbt.contains("Trim")) {
                    NbtCompound trimNbt = nbt.getCompound("Trim");
                    // Check if the trim material is gold
                    // In 1.20.1, trim material is stored as "material" in the Trim compound
                    if (trimNbt.contains("material")) {
                        String material = trimNbt.getString("material");
                        // Check for gold trim (minecraft:gold)
                        if (material.equals("minecraft:gold")) {
                            cir.setReturnValue(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}

