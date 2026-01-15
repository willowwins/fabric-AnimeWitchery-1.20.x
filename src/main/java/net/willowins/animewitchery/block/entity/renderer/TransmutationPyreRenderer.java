package net.willowins.animewitchery.block.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.willowins.animewitchery.block.entity.TransmutationPyreBlockEntity;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.awt.Color;

@Environment(EnvType.CLIENT)
public class TransmutationPyreRenderer implements BlockEntityRenderer<TransmutationPyreBlockEntity> {

    public TransmutationPyreRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(TransmutationPyreBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.isActive()) {
            renderPyreBeam(entity);
        }
    }

    private void renderPyreBeam(TransmutationPyreBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null || !world.isClient)
            return;

        BlockPos pos = entity.getPos();
        Vec3d center = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        // Spawn particles for the beam
        // Spawning fewer particles per frame than the barrier since it's a smaller
        // block, but still impressive
        int resultType = entity.getResultType();
        Color color1;
        Color color2;

        if (resultType == 2) { // Haloic: Gold/Orange
            color1 = new Color(255, 215, 0);
            color2 = new Color(255, 140, 0);
        } else if (resultType == 1) { // Netherite: Red/Dark Red
            color1 = new Color(255, 0, 0);
            color2 = new Color(139, 0, 0);
        } else { // Normal: Purple (BlueViolet to Indigo)
            color1 = new Color(138, 43, 226);
            color2 = new Color(75, 0, 130);
        }

        // Spawn particles for the beam
        // Spawning fewer particles per frame than the barrier since it's a smaller
        // block, but still impressive
        for (int i = 0; i < 5; i++) {
            double x = center.x + (Math.random() - 0.5) * 0.8; // 0.8 block width
            double y = center.y + 0.5 + Math.random() * 2.0;
            double z = center.z + (Math.random() - 0.5) * 0.8;

            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.2f, 0).build())
                    .setTransparencyData(GenericParticleData.create(1.0f, 0.0f).build())
                    .setColorData(ColorParticleData.create(color1, color2)
                            .setCoefficient(1.2f).setEasing(Easing.EXPO_OUT).build())
                    .setLifetime(40)
                    .addMotion(0, 0.4, 0) // Upward motion
                    .enableNoClip()
                    .spawn(world, x, y, z);
        }
    }
}
