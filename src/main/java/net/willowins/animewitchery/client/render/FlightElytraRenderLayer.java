package net.willowins.animewitchery.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.enchantments.ModEnchantments;
import net.willowins.animewitchery.item.armor.ResonantArmorItem;

public class FlightElytraRenderLayer
        extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier TEXTURE = new Identifier("textures/entity/elytra.png");
    private final ElytraEntityModel<AbstractClientPlayerEntity> elytraModel;

    public FlightElytraRenderLayer(FeatureRendererContext<AbstractClientPlayerEntity,
            PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.elytraModel = new ElytraEntityModel<>(
                MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.ELYTRA)
        );
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                       AbstractClientPlayerEntity player, float limbAngle, float limbDistance,
                       float tickDelta, float animationProgress, float headYaw, float headPitch) {

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof ArmorItem armor)) return;

        // ✨ Skip rendering for Resonant Armor chestplates entirely
        if (armor instanceof ResonantArmorItem) return;

        // Only render if enchanted with Flight enchantment
        int level = EnchantmentHelper.getLevel(ModEnchantments.FLIGHT_ENCHANT, chest);
        if (level <= 0) return;

        // Proceed with Elytra visual rendering
        matrices.push();
        getContextModel().copyStateTo(this.elytraModel);
        this.elytraModel.setAngles(player, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

        var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(TEXTURE));
        this.elytraModel.render(matrices, vertexConsumer, light,
                net.minecraft.client.render.OverlayTexture.DEFAULT_UV,
                1.0f, 1.0f, 1.0f, 1.0f);
        matrices.pop();
    }
}
