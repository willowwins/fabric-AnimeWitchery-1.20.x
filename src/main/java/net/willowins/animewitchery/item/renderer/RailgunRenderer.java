package net.willowins.animewitchery.item.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.item.custom.HealingStaff;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.willowins.animewitchery.item.client.model.RailgunModel;
import net.willowins.animewitchery.item.custom.RailgunItem;

public class RailgunRenderer extends GeoItemRenderer<RailgunItem> {
    public RailgunRenderer() {
        super(new RailgunModel());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack,
            VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        if (transformType.isFirstPerson() && stack.getOrCreateNbt().getFloat("charge") > 0.0f
                && stack.getOrCreateNbt().getFloat("charge") < 1.0f) {
            poseStack.push();

            poseStack.translate(0, 0.6, 0);
            super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.pop();
            return;
        }
        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
