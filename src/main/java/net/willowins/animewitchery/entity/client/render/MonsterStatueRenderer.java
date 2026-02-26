package net.willowins.animewitchery.entity.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.entity.client.model.MonsterStatueModel;
import net.willowins.animewitchery.entity.custom.MonsterStatueBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MonsterStatueRenderer extends GeoBlockRenderer<MonsterStatueBlockEntity> {
    public MonsterStatueRenderer(net.minecraft.client.render.block.entity.BlockEntityRendererFactory.Context context) {
        super(new MonsterStatueModel());
    }
}
