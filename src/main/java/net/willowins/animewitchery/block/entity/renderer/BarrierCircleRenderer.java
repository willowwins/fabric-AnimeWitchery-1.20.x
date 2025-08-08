package net.willowins.animewitchery.block.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.willowins.animewitchery.AnimeWitchery;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.easing.Easing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import java.awt.Color;

public class BarrierCircleRenderer implements BlockEntityRenderer<BarrierCircleBlockEntity> {
    
    // Texture identifiers for different stages
    private static final Identifier BASIC_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_basic.png");
    private static final Identifier DEFINED_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_defined.png");
    private static final Identifier COMPLETE_CIRCLE_TEXTURE = new Identifier(AnimeWitchery.MOD_ID, "textures/block/barrier_circle_complete.png");

    public BarrierCircleRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(BarrierCircleBlockEntity entity, float tickDelta, MatrixStack matrices, 
                      VertexConsumerProvider vertexConsumers, int light, int overlay) {
        
        // Get the current stage to determine which texture to use
        BarrierCircleBlockEntity.CircleStage stage = entity.getStage();
        Identifier textureToUse;
        
        switch (stage) {
            case BASIC:
                textureToUse = BASIC_CIRCLE_TEXTURE;
                break;
            case DEFINED:
                textureToUse = DEFINED_CIRCLE_TEXTURE;
                break;
            case COMPLETE:
                textureToUse = COMPLETE_CIRCLE_TEXTURE;
                break;
            default:
                textureToUse = BASIC_CIRCLE_TEXTURE;
        }
        // Render the circle texture as a flat overlay on the ground
        renderCircleOverlay(matrices, vertexConsumers, textureToUse, light, overlay);
        
        // Add ritual particle effects if in ritual phase and ritual is active
        if (stage == BarrierCircleBlockEntity.CircleStage.COMPLETE && entity.isRitualActive()) {
            int ritualStep = entity.getRitualActivationStep();
            if (ritualStep >= 3 && ritualStep < 4) { // Gathering phase (step 3 only)
                renderGatheringParticles(entity);
                renderEnergyBall(entity); // Add energy ball in center
            }
            if (ritualStep >= 4) { // Beam phase (step 4+)
                renderGiantBeam(entity);
            }
        }
    }
    
    private void renderCircleOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                                   Identifier texture, int light, int overlay) {
        
        // Get the vertex consumer for the texture with proper texture binding
        var renderLayer = RenderLayer.getEntityTranslucentCull(texture);
        var vertexConsumer = vertexConsumers.getBuffer(renderLayer);
                
        // Create a flat quad slightly above the ground (Y + 0.01)
        float y = 0.01f;
        
        // Define the quad vertices (11x11 block size) - centered on the block
        float minX = -6.5f;
        float maxX = 6.5f;  // 13 blocks wide
        float minZ = -6.5f;
        float maxZ = 6.5f;  // 13 blocks deep
        
        // UV coordinates for the texture (128x128 texture will be automatically scaled)
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 1.0f;  // Flipped V coordinates to fix upside-down texture
        float maxV = 0.0f;  // Flipped V coordinates to fix upside-down texture
        
        // Render the quad
        matrices.push();
        
        // Center the circle on the block by offsetting the rendering position
        matrices.translate(0.5f, 0, 0.5f);
        
        // Top-left vertex (first)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), minX, y, maxZ)
                .color(255, 255, 255, 255)
                .texture(minU, minV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Top-right vertex (second)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), maxX, y, maxZ)
                .color(255, 255, 255, 255)
                .texture(maxU, minV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Bottom-right vertex (third)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), maxX, y, minZ)
                .color(255, 255, 255, 255)
                .texture(maxU, maxV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        // Bottom-left vertex (fourth)
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), minX, y, minZ)
                .color(255, 255, 255, 255)
                .texture(minU, maxV)
                .overlay(overlay)
                .light(light)
                .normal(matrices.peek().getNormalMatrix(), 0, 1, 0)  // Face up so it's visible from above
                .next();
        
        matrices.pop();
    }
    
    private void renderGatheringParticles(BarrierCircleBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null || world.isClient == false) return;
        
        BlockPos circlePos = entity.getPos();
        BlockPos[] obeliskPositions = {
            circlePos.north(5), // North
            circlePos.east(5),  // East
            circlePos.south(5), // South
            circlePos.west(5)   // West
        };
        
        Vec3d center = new Vec3d(circlePos.getX() + 0.5, circlePos.getY() + 0.5, circlePos.getZ() + 0.5);
        
        for (BlockPos obeliskPos : obeliskPositions) {
            Vec3d obeliskCenter = new Vec3d(obeliskPos.getX() + 0.5, obeliskPos.getY() + 1.0, obeliskPos.getZ() + 0.5);
            Vec3d direction = center.subtract(obeliskCenter).normalize();
            
            // Spawn particles flowing from obelisk to center
            for (int i = 0; i < 10; i++) {
                double progress = (world.getTime() % 20) / 20.0; // 1-second cycle
                Vec3d particlePos = obeliskCenter.lerp(center, progress);
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.1f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.8f, 0.2f).build())
                    .setColorData(ColorParticleData.create(new Color(128, 0, 255), new Color(255, 100, 255)).setCoefficient(1.2f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setLifetime(20)
                    .addMotion(direction.x * 0.1, direction.y * 0.1, direction.z * 0.1)
                    .enableNoClip()
                    .spawn(world, particlePos.x, particlePos.y, particlePos.z);
            }
        }
    }
    
    private void renderGiantBeam(BarrierCircleBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null || world.isClient == false) return;
        
        BlockPos circlePos = entity.getPos();
        Vec3d center = new Vec3d(circlePos.getX() + 0.5, circlePos.getY() + 0.5, circlePos.getZ() + 0.5);
        
        // Create a massive beam shooting upward
        for (int i = 0; i < 30; i++) {
            double x = center.x + (Math.random() - 0.5) * 1.5; // 1.5-block wide beam
            double y = center.y + Math.random() * 5.0; // 5 blocks tall!
            double z = center.z + (Math.random() - 0.5) * 1.5;
            
            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                .setScaleData(GenericParticleData.create(0.2f, 0).build())
                .setTransparencyData(GenericParticleData.create(1.0f, 0.1f).build())
                .setColorData(ColorParticleData.create(new Color(100, 100, 255), new Color(0, 180, 255)).setCoefficient(1.5f).setEasing(Easing.BOUNCE_IN_OUT).build())
                .setLifetime(40)
                .addMotion(0, 1.0, 0) // Strong upward motion
                .enableNoClip()
                .spawn(world, x, y, z);
        }
    }
    
    private void renderEnergyBall(BarrierCircleBlockEntity entity) {
        World world = entity.getWorld();
        if (world == null || world.isClient == false) return;
        
        BlockPos circlePos = entity.getPos();
        Vec3d center = new Vec3d(circlePos.getX() + 0.5, circlePos.getY() + 1.0, circlePos.getZ() + 0.5);
        
        float time = world.getTime() * 0.1f;
        
        // Calculate growth based on actual time since step 3 started
        int ritualStep = entity.getRitualActivationStep();
        if (ritualStep == 3) {
            long step3StartTime = entity.getStep3StartTime();
            long currentTime = world.getTime();
            long timeSinceStep3 = currentTime - step3StartTime;
            
            // Cap the charging time at 200 ticks (10 seconds)
            float chargeProgress = Math.min(timeSinceStep3 / 200.0f, 1.0f);
            float baseRadius = 0.5f + chargeProgress * 3.0f; // Grows from 0.5 to 3.5 blocks
            
            // Create a growing spinning energy ball in the center
            for (int i = 0; i < 15; i++) {
                float angle = time + (i * 0.42f); // Distribute particles around the ball
                float radius = baseRadius + (float)Math.sin(time * 2) * 0.3f; // Pulsing radius on top of growth
                
                double x = center.x + Math.cos(angle) * radius;
                double y = center.y + Math.sin(time * 3 + i) * 0.5; // Vertical oscillation
                double z = center.z + Math.sin(angle) * radius;
                
                // Scale particles based on charge progress
                float particleScale = 0.2f + chargeProgress * 0.4f; // Particles get bigger too
                
                // Create the energy ball particles
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(particleScale, 0).build())
                    .setTransparencyData(GenericParticleData.create(1.0f, 0.2f).build())
                    .setColorData(ColorParticleData.create(new Color(255, 255, 100), new Color(255, 100, 255)).setCoefficient(1.8f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.8f, 1.5f).setSpinOffset(time + i).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(30)
                    .addMotion(0, 0.05f, 0) // Slight upward drift
                    .enableNoClip()
                    .spawn(world, x, y, z);
            }
            
            // Add some core particles in the very center (also grow)
            for (int i = 0; i < 5; i++) {
                float coreScale = 0.3f + chargeProgress * 0.7f; // Core particles grow more
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(coreScale, 0).build())
                    .setTransparencyData(GenericParticleData.create(1.0f, 0.1f).build())
                    .setColorData(ColorParticleData.create(new Color(255, 255, 255), new Color(255, 200, 100)).setCoefficient(2.0f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(1.0f, 2.0f).setSpinOffset(time * 2 + i).setEasing(Easing.QUARTIC_IN).build())
                .setLifetime(40)
                    .addMotion(0, 0.02f, 0)
                    .enableNoClip()
                    .spawn(world, center.x, center.y, center.z);
            }
        }
        // If ritualStep > 3, the ball disappears (beam phase)
    }
} 