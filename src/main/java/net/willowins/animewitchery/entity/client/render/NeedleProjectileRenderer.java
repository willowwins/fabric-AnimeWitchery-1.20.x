package net.willowins.animewitchery.entity.client.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.willowins.animewitchery.entity.projectile.NeedleProjectileEntity;
import net.willowins.animewitchery.entity.client.model.NeedleProjectileGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.willowins.animewitchery.entity.ModEntities;
import net.willowins.animewitchery.item.ModItems;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;

public class NeedleProjectileRenderer extends GeoEntityRenderer<NeedleProjectileEntity> {

        public NeedleProjectileRenderer(EntityRendererFactory.Context ctx) {
                super(ctx, new NeedleProjectileGeoModel());
                this.shadowRadius = 0.25f;
        }

        @Override
        public void render(NeedleProjectileEntity entity, float entityYaw, float partialTick, MatrixStack matrices,
                        net.minecraft.client.render.VertexConsumerProvider vertexConsumers, int light) {

                // Render the Needle Model
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                                MathHelper.lerp(partialTick, entity.prevYaw, entity.getYaw()) + 90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Z
                                .rotationDegrees(MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch())));
                super.render(entity, entityYaw, partialTick, matrices, vertexConsumers, light);
                matrices.pop();

                // Render the Chain/Thread
                net.minecraft.entity.Entity owner = entity.getOwner();
                if (owner instanceof net.minecraft.entity.player.PlayerEntity player) {
                        renderChain(entity, player, partialTick, matrices, vertexConsumers, light);
                }
        }

        private void renderChain(NeedleProjectileEntity entity, net.minecraft.entity.LivingEntity owner,
                        float partialTick, MatrixStack matrices,
                        net.minecraft.client.render.VertexConsumerProvider vertexConsumers, int light) {
                Vec3d pos = entity.getLerpedPos(partialTick);
                // Rotations for needle
                float yawAngle = MathHelper.lerp(partialTick, entity.prevYaw, entity.getYaw());
                float pitchAngle = MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch());

                // Offset to back of needle - Inverting to fix "wrong end" issue
                Vec3d ringPos = new Vec3d(0.5, 0, 0)
                                .rotateZ(pitchAngle * MathHelper.RADIANS_PER_DEGREE)
                                .rotateY((yawAngle + 90) * MathHelper.RADIANS_PER_DEGREE)
                                .add(0, entity.getHeight() / 2f, 0);

                // Owner Hand Position Logic (Simplified)
                double handOffset = (owner.getMainArm() == net.minecraft.util.Arm.RIGHT ? 1 : -1) * 0.35;
                if (owner.getMainHandStack().getItem() != ModItems.NEEDLE) {
                        handOffset = -handOffset;
                }
                float bodyYaw = MathHelper.lerp(partialTick, owner.prevBodyYaw, owner.bodyYaw)
                                * MathHelper.RADIANS_PER_DEGREE;
                double handX = Math.cos(bodyYaw) * handOffset;
                double handZ = Math.sin(bodyYaw) * handOffset;

                Vec3d leashPos = owner.getLeashPos(partialTick).add(handX, -0.3, handZ); // Rough adjustment to hand
                                                                                         // height/side

                // Vector from Needle to Hand
                Vec3d ownerPos = leashPos.subtract(pos);

                matrices.push();

                MatrixStack.Entry matrixEntry = matrices.peek();
                Matrix4f modelMatrix = matrixEntry.getPositionMatrix();
                org.joml.Matrix3f normal = matrixEntry.getNormalMatrix();

                // Use LINES render layer
                // Note: getLines() expects 2 vertices per line.
                net.minecraft.client.render.VertexConsumer vertexConsumer = vertexConsumers
                                .getBuffer(RenderLayer.getLines());

                // Draw Line
                // Color: Silver/White (200, 200, 200)
                vertexConsumer.vertex(modelMatrix, (float) ringPos.x, (float) ringPos.y, (float) ringPos.z)
                                .color(200, 200, 200, 255)
                                .normal(normal, 0, 1, 0)
                                .next();

                vertexConsumer.vertex(modelMatrix, (float) ownerPos.x, (float) ownerPos.y, (float) ownerPos.z)
                                .color(200, 200, 200, 255)
                                .normal(normal, 0, 1, 0)
                                .next();

                matrices.pop();
        }
}
