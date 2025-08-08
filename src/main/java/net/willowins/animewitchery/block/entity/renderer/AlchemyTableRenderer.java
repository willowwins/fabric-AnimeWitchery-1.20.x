package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.block.entity.AlchemyTableBlockEntity;
import net.willowins.animewitchery.block.entity.BindingSpellBlockEntity;
import net.willowins.animewitchery.block.entity.model.AlchemyTableModel;
import net.willowins.animewitchery.block.entity.model.BindingSpellModel;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AlchemyTableRenderer extends GeoBlockRenderer<AlchemyTableBlockEntity> {
    private final ItemRenderer itemRenderer;
    
    public AlchemyTableRenderer(BlockEntityRendererFactory.Context context) {
        super(new AlchemyTableModel());
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void renderFinal(MatrixStack poseStack, AlchemyTableBlockEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        
        // Render floating item from slot 0
        ItemStack itemStack = animatable.getStack(0);
        if (!itemStack.isEmpty()) {
            renderFloatingItem(itemStack, animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
    
    private void renderFloatingItem(ItemStack itemStack, AlchemyTableBlockEntity blockEntity, float partialTick, 
                                   MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
        poseStack.push();
        
        // Position the item above the table
        poseStack.translate(0.5, 1.5, 0.5);
        
        // Add floating animation
        float time = (blockEntity.getWorld().getTime() + partialTick) * 0.05f;
        float yOffset = MathHelper.sin(time) * 0.1f;
        poseStack.translate(0, yOffset, 0);
        
        // Add rotation animation
        poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(time * 20));
        
        // Scale the item
        poseStack.scale(0.6f, 0.6f, 0.6f);
        
        // Render the item
        itemRenderer.renderItem(itemStack, ModelTransformationMode.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getWorld(), 0);
        
        poseStack.pop();
    }
}
