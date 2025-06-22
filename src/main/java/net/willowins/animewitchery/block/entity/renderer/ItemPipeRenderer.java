package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.block.entity.ItemPipeBlockEntity;
import net.willowins.animewitchery.block.entity.model.ItemPipeModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ItemPipeRenderer extends GeoBlockRenderer<ItemPipeBlockEntity> {
    public ItemPipeRenderer(BlockEntityRendererFactory.Context context) {
        super(new ItemPipeModel());
    }

    @Override
    public RenderLayer getRenderType(ItemPipeBlockEntity animatable, Identifier texture,
                                     VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityCutout(texture);
    }

}
