package net.willowins.animewitchery.entity.client.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class NeedleModel extends Model {
    private final ModelPart root;

    public NeedleModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("quad1", ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-0.5f, -0.5f, -6f, 1f, 1f, 12f), ModelTransform.NONE);

        root.addChild("quad2", ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(-0.5f, -0.5f, -6f, 1f, 1f, 12f)
                .mirrored(), ModelTransform.rotation(0, (float) Math.PI / 2, 0));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}