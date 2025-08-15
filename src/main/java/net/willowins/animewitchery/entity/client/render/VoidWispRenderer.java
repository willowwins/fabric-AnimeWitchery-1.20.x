package net.willowins.animewitchery.entity.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.entity.VoidWispEntity;
import net.willowins.animewitchery.entity.client.model.VoidWispModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.data.color.*;
import team.lodestar.lodestone.systems.particle.data.spin.*;
import java.awt.Color;

public class VoidWispRenderer extends GeoEntityRenderer<VoidWispEntity> {
    public VoidWispRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new VoidWispModel());
    }

    @Override
    public Identifier getTextureLocation(VoidWispEntity animatable) {
        return new Identifier(AnimeWitchery.MOD_ID, "textures/entity/void_wisp.png");
    }

    @Override
    public void render(VoidWispEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        // Add void-like Lodestone particles around the entity
        World world = entity.getWorld();
        if (world != null && world.isClient) {
            float time = world.getTime() * 0.1f;
            
            // Create void essence particles around the entity
            for (int i = 0; i < 6; i++) {
                float angle = time + (i * 1.05f); // 60 degrees apart
                float radius = 0.8f + (float) Math.sin(time * 0.5 + i) * 0.2f;
                
                double x = entity.getX() + Math.cos(angle) * radius;
                double y = entity.getY() + 0.5 + Math.sin(time * 0.3 + i) * 0.3f;
                double z = entity.getZ() + Math.sin(angle) * radius;
                
                // Pure black void particles
                Color voidColor = new Color(0, 0, 0); // Pure black
                Color voidFade = new Color(20, 20, 20); // Very dark gray
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.12f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.9f, 0.2f).build())
                    .setColorData(ColorParticleData.create(voidColor, voidFade).setCoefficient(1.1f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.3f, 0.6f).setSpinOffset(angle).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(35)
                    .addMotion(0, 0.008f, 0)
                    .enableNoClip()
                    .spawn(world, x, y, z);
            }
            
            // Add floating void motes above the entity
            if (world.getTime() % 2 == 0) {
                for (int i = 0; i < 3; i++) {
                    double offsetX = (Math.random() - 0.5) * 0.8;
                    double offsetZ = (Math.random() - 0.5) * 0.8;
                    
                    Color moteColor = new Color(0, 0, 0); // Pure black
                    Color fadeColor = new Color(15, 15, 15); // Very dark gray
                    
                    WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                        .setScaleData(GenericParticleData.create(0.06f, 0).build())
                        .setTransparencyData(GenericParticleData.create(0.95f, 0.1f).build())
                        .setColorData(ColorParticleData.create(moteColor, fadeColor).setCoefficient(1.0f).setEasing(Easing.LINEAR).build())
                        .setSpinData(SpinParticleData.create(0.15f, 0.3f).setSpinOffset(time + i).setEasing(Easing.SINE_IN_OUT).build())
                        .setLifetime(50)
                        .addMotion(0, 0.015f, 0)
                        .enableNoClip()
                        .spawn(world, entity.getX() + offsetX, entity.getY() + 1.4, entity.getZ() + offsetZ);
                }
            }
            
            // Add void tendrils that swirl around the entity
            for (int i = 0; i < 4; i++) {
                float tendrilAngle = time * 0.8f + (i * 1.57f); // 90 degrees apart
                float tendrilRadius = 1.2f + (float) Math.sin(time * 0.4 + i) * 0.4f;
                float tendrilHeight = (float) Math.sin(time * 0.6 + i) * 0.5f;
                
                double tendrilX = entity.getX() + Math.cos(tendrilAngle) * tendrilRadius;
                double tendrilY = entity.getY() + 0.8 + tendrilHeight;
                double tendrilZ = entity.getZ() + Math.sin(tendrilAngle) * tendrilRadius;
                
                Color tendrilColor = new Color(0, 0, 0); // Pure black
                Color tendrilTip = new Color(25, 25, 25); // Very dark gray tip
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.1f, 0.01f).build())
                    .setTransparencyData(GenericParticleData.create(0.8f, 0.15f).build())
                    .setColorData(ColorParticleData.create(tendrilColor, tendrilTip).setCoefficient(1.2f).setEasing(Easing.EXPO_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.5f, 0.9f).setSpinOffset(tendrilAngle).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(25)
                    .addMotion(0, 0.003f, 0)
                    .enableNoClip()
                    .spawn(world, tendrilX, tendrilY, tendrilZ);
            }
        }
    }
}
