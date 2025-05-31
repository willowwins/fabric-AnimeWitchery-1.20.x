package net.willowins.animewitchery.entity.client.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;
import net.willowins.animewitchery.entity.client.model.NeedleProjectileGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class NeedleProjectileRenderer extends GeoEntityRenderer<NeedleProjectileEntity> {
    public NeedleProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new NeedleProjectileGeoModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    protected void applyRotations(NeedleProjectileEntity entity, MatrixStack matrices, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity, matrices, ageInTicks, rotationYaw, partialTick);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getPitch()));
    }
}