package net.willowins.animewitchery.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.willowins.animewitchery.effect.ModEffect;
import net.willowins.animewitchery.util.VoidPhaseUtil;
import net.minecraft.item.ItemStack;
import net.willowins.animewitchery.item.custom.CosmeticBagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void onRender(AbstractClientPlayerEntity entity, float f, float g,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, CallbackInfo ci) {
        // AbstractClientPlayerEntity extends Entity -> has public int age
        float voidPhase = VoidPhaseUtil.computePhase(entity.age, 0);
        if (voidPhase > 0.99f && entity.hasStatusEffect(ModEffect.VOID_BOUND)) {
            ci.cancel(); // cancel all rendering for this player
        }
    }

    @Inject(method = "setModelPose", at = @At("RETURN"))
    private void restoreSkinLayers(AbstractClientPlayerEntity player, CallbackInfo ci) {
        if (player.isSpectator()) {
            return;
        }

        PlayerEntityRenderer renderer = (PlayerEntityRenderer) (Object) this;
        PlayerEntityModel<AbstractClientPlayerEntity> model = renderer.getModel();

        // Check for Cosmetic Bag
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof net.willowins.animewitchery.item.custom.CosmeticBagItem) {
                net.minecraft.nbt.NbtCompound nbt = stack.getNbt();
                if (nbt != null && nbt.contains("Items")) {
                    net.minecraft.nbt.NbtList list = nbt.getList("Items", 10);

                    for (int k = 0; k < list.size(); k++) {
                        net.minecraft.nbt.NbtCompound itemNbt = list.getCompound(k);
                        int slot = itemNbt.getByte("Slot") & 255;
                        ItemStack vanityStack = ItemStack.fromNbt(itemNbt);

                        if (vanityStack.getItem() == net.minecraft.item.Items.GLASS) {
                            // Restore visibility if the part is enabled in skin settings
                            if (slot == 0) { // HEAD
                                if (player.isPartVisible(net.minecraft.client.render.entity.PlayerModelPart.HAT)) {
                                    model.hat.visible = true;
                                }
                            } else if (slot == 1) { // CHEST
                                if (player.isPartVisible(net.minecraft.client.render.entity.PlayerModelPart.JACKET)) {
                                    model.jacket.visible = true;
                                }
                                if (player.isPartVisible(
                                        net.minecraft.client.render.entity.PlayerModelPart.LEFT_SLEEVE)) {
                                    model.leftSleeve.visible = true;
                                }
                                if (player.isPartVisible(
                                        net.minecraft.client.render.entity.PlayerModelPart.RIGHT_SLEEVE)) {
                                    model.rightSleeve.visible = true;
                                }
                            } else if (slot == 2) { // LEGS
                                if (player.isPartVisible(
                                        net.minecraft.client.render.entity.PlayerModelPart.LEFT_PANTS_LEG)) {
                                    model.leftPants.visible = true;
                                }
                                if (player.isPartVisible(
                                        net.minecraft.client.render.entity.PlayerModelPart.RIGHT_PANTS_LEG)) {
                                    model.rightPants.visible = true;
                                }
                            }
                        }
                    }
                    // Break after finding the bag (first valid bag prioritizes)
                    return;
                }
            }
        }
    }
}