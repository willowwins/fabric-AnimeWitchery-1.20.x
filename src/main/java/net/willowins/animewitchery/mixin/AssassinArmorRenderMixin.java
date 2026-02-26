package net.willowins.animewitchery.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.willowins.animewitchery.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class AssassinArmorRenderMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void hideArmorForAssassin(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
            LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        // Check for Invisibility
        if (livingEntity.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            // Check for Assassin Hood
            if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).getItem() == ModItems.ASSASSIN_HOOD) {
                // Cancel rendering of armor
                ci.cancel();
            }
        }
    }
}
