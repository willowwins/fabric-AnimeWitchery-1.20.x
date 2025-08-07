package net.willowins.animewitchery.block.entity.renderer;

import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.model.ActiveObeliskModel;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.block.entity.BlockEntity;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.systems.easing.*;
import team.lodestar.lodestone.systems.particle.builder.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.data.color.*;
import team.lodestar.lodestone.systems.particle.data.spin.*;
import java.awt.Color;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.BarrierCircleBlockEntity;
import java.util.ArrayList;
import java.util.List;

public class ActiveObeliskRenderer extends GeoBlockRenderer<ActiveObeliskBlockEntity> {
    public ActiveObeliskRenderer() {
        super(new ActiveObeliskModel());
    }

    @Override
    public void preRender(MatrixStack poseStack, ActiveObeliskBlockEntity animatable, BakedGeoModel model,
            VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
            int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        // Add Lodestone effects here!
        // This runs on CLIENT-SIDE only
        BlockPos pos = animatable.getPos();
        World world = animatable.getWorld();
        
        // Create a beautiful spiral effect around the obelisk!
        float time = world.getTime() * 0.1f;
        Color startingColor = new Color(100, 0, 100);
        Color endingColor = new Color(0, 100, 200);
        
         // Spiral particles around the obelisk
        for (int i = 0; i < 8; i++) {
            float angle = time + (i * 0.785f); // 45 degrees apart
            float height = (time * 0.3f + i * 0.3f) % 4; // Reduced height range
            float radius = 1.5f + (height * 0.05f); // Smaller radius scaling
        
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double y = pos.getY() + height;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            
            WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.1f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.8f, 0.2f).build())
                    .setColorData(ColorParticleData.create(startingColor, endingColor).setCoefficient(1.4f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    .setSpinData(SpinParticleData.create(0.3f, 0.6f).setSpinOffset(angle).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(60)
                    .addMotion(0, 0.02f, 0)
                    .enableNoClip()
                    .spawn(world, x, y, z);
        }
        
        // Add some particles at the top of the obelisk
        //  WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
        //          .setScaleData(GenericParticleData.create(0.3f, 0).build())
        //          .setTransparencyData(GenericParticleData.create(0.9f, 0.1f).build())
        //          .setColorData(ColorParticleData.create(new Color(255, 255, 100), new Color(255, 100, 255)).setCoefficient(1.2f).setEasing(Easing.BOUNCE_IN_OUT).build())
        //          .setSpinData(SpinParticleData.create(0.5f, 1.0f).setSpinOffset(time * 2).setEasing(Easing.QUARTIC_IN).build())
        //          .setLifetime(80)
        //          .addMotion(0, 0.03f, 0)
        //          .enableNoClip()
        //          .spawn(world, pos.getX() + 0.5, pos.getY() + 4, pos.getZ() + 0.5);

        // Create connections to nearby active obelisks only if part of an active ritual!
        if (world.getTime() % 60 == 0) { // Check every 3 seconds
            // Check if this obelisk is part of an active ritual
            boolean isPartOfActiveRitual = false;
            
            // Check for nearby barrier circles with active rituals
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    BlockPos checkPos = pos.add(x, 0, z);
                    BlockState checkState = world.getBlockState(checkPos);
                    
                    if (checkState.isOf(ModBlocks.BARRIER_CIRCLE)) {
                        BlockEntity blockEntity = world.getBlockEntity(checkPos);
                        if (blockEntity instanceof BarrierCircleBlockEntity circleEntity) {
                            if (circleEntity.isRitualActive()) {
                                // Check if this obelisk is one of the ritual obelisks
                                BlockPos circlePos = circleEntity.getPos();
                                BlockPos northPos = circlePos.north(5);
                                BlockPos southPos = circlePos.south(5);
                                BlockPos eastPos = circlePos.east(5);
                                BlockPos westPos = circlePos.west(5);
                                
                                if (pos.equals(northPos) || pos.equals(southPos) || 
                                    pos.equals(eastPos) || pos.equals(westPos)) {
                                    isPartOfActiveRitual = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (isPartOfActiveRitual) break;
            }
            
            // Only create connections if part of an active ritual
            if (isPartOfActiveRitual) {
                List<BlockPos> nearbyActiveObelisks = findNearbyActiveObelisks(world, pos, 48); // 48 block radius
                
                // Check if we have 5 or more obelisks for a dynamic pentagram
                if (nearbyActiveObelisks.size() >= 5) {
                    createDynamicPentagramConnections(world, nearbyActiveObelisks, time);
                } else if (nearbyActiveObelisks.size() >= 2) {
                    // Neighbor connections for 2-4 obelisks
                    createNeighborConnections(world, nearbyActiveObelisks, time);
                }
            }
        }
    }
    
    private static List<BlockPos> findNearbyActiveObelisks(World world, BlockPos center, int radius) {
        List<BlockPos> foundObelisks = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = center.add(x, y, z);
                    if (world.getBlockState(checkPos).isOf(ModBlocks.ACTIVE_OBELISK)) {
                        foundObelisks.add(checkPos);
                    }
                }
            }
        }
        
        return foundObelisks;
    }
    
         private static void createNeighborConnections(World world, List<BlockPos> obelisks, float time) {
         // Sort obelisks by angle from center to create neighbor connections
         BlockPos center = calculateCenter(obelisks);
         List<BlockPos> sortedObelisks = sortObelisksByAngle(obelisks, center);
         
         int numObelisks = sortedObelisks.size();
         
         // Connect each obelisk to its neighbors in a circle
         for (int i = 0; i < numObelisks; i++) {
             int nextIndex = (i + 1) % numObelisks; // Connect to next neighbor
             BlockPos pos1 = sortedObelisks.get(i);
             BlockPos pos2 = sortedObelisks.get(nextIndex);
             
             // Create neighbor connection particles
             createConnectionParticles(world, pos1, pos2, time, i);
         }
     }
     
     private static void createConnectionParticles(World world, BlockPos pos1, BlockPos pos2, float time, int connectionIndex) {
        // Calculate the center points of both obelisks (they're 16 blocks tall)
        double x1 = pos1.getX() + 0.5;
        double y1 = pos1.getY() + 2; // Middle of the obelisk
        double z1 = pos1.getZ() + 0.5;
        
        double x2 = pos2.getX() + 0.5;
        double y2 = pos2.getY() + 2; // Middle of the obelisk
        double z2 = pos2.getZ() + 0.5;
        
        // Create multiple particle streams along the connection
        for (int stream = 0; stream < 3; stream++) {
            double streamOffset = (stream - 1) * 0.3; // Offset each stream slightly
            
                         for (int i = 0; i < 15; i++) {
                 double progress = (time * 0.05 + i * 0.067 + stream * 0.33) % 1.0;
                
                // Calculate position along the connection line
                double particleX = x1 + (x2 - x1) * progress;
                double particleY = y1 + (y2 - y1) * progress;
                double particleZ = z1 + (z2 - z1) * progress;
                
                                 // Add slight offset for multiple streams (but keep lines straight)
                 double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
                 if (distance > 0) {
                     double perpX = -(z2 - z1) / distance * streamOffset;
                     double perpZ = (x2 - x1) / distance * streamOffset;
                     particleX += perpX;
                     particleZ += perpZ;
                 }
                
                                 // Create beautiful connection particles with color variation
                 Color connectionColor = new Color(150, 50, 255); // Purple connection
                 Color pulseColor = new Color(255, 100, 255); // Pink pulse
                 
                 // Add slight color variation based on connection index
                 float hue = (connectionIndex * 60.0f) % 360.0f; // 60 degrees apart
                 Color variedColor = Color.getHSBColor(hue / 360.0f, 0.7f, 1.0f);
                
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setScaleData(GenericParticleData.create(0.1f, 0).build())
                    .setTransparencyData(GenericParticleData.create(0.7f, 0.1f).build())
                    .setColorData(ColorParticleData.create(variedColor, pulseColor).setCoefficient(1.3f).setEasing(Easing.BOUNCE_IN_OUT).build())
                    //.setSpinData(SpinParticleData.create(0.4f, 0.8f).setSpinOffset(time + i).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(20)
                    .addMotion(0, 0, 0)
                    .enableNoClip()
                    .spawn(world, particleX, particleY, particleZ);
             }
         }
     }
     
     private static void createDynamicPentagramConnections(World world, List<BlockPos> obelisks, float time) {
         // Sort obelisks by angle from center to create dynamic pentagram pattern
         BlockPos center = calculateCenter(obelisks);
         List<BlockPos> sortedObelisks = sortObelisksByAngle(obelisks, center);
         
         int numObelisks = sortedObelisks.size();
         
         // Create dynamic pentagram connections based on number of obelisks
         // For 5 obelisks: 0->2, 1->3, 2->4, 3->0, 4->1 (classic pentagram)
         // For 6 obelisks: 0->3, 1->4, 2->5, 3->0, 4->1, 5->2 (hexagram)
         // For 7 obelisks: 0->3, 1->4, 2->5, 3->6, 4->0, 5->1, 6->2 (heptagram)
         // And so on...
         
         for (int i = 0; i < numObelisks; i++) {
             int targetIndex = (i + (numObelisks / 2)) % numObelisks; // Connect to opposite side
             BlockPos pos1 = sortedObelisks.get(i);
             BlockPos pos2 = sortedObelisks.get(targetIndex);
             
             // Create special dynamic pentagram particles
             createDynamicPentagramParticles(world, pos1, pos2, time, i, numObelisks);
         }
     }
     
     private static BlockPos calculateCenter(List<BlockPos> obelisks) {
         double centerX = 0, centerY = 0, centerZ = 0;
         for (BlockPos pos : obelisks) {
             centerX += pos.getX();
             centerY += pos.getY();
             centerZ += pos.getZ();
         }
         return new BlockPos((int)(centerX / obelisks.size()), (int)(centerY / obelisks.size()), (int)(centerZ / obelisks.size()));
     }
     
     private static List<BlockPos> sortObelisksByAngle(List<BlockPos> obelisks, BlockPos center) {
         List<BlockPos> sorted = new ArrayList<>(obelisks);
         sorted.sort((pos1, pos2) -> {
             double angle1 = Math.atan2(pos1.getZ() - center.getZ(), pos1.getX() - center.getX());
             double angle2 = Math.atan2(pos2.getZ() - center.getZ(), pos2.getX() - center.getX());
             return Double.compare(angle1, angle2);
         });
         return sorted;
     }
     
     private static void createDynamicPentagramParticles(World world, BlockPos pos1, BlockPos pos2, float time, int connectionIndex, int totalObelisks) {
         // Calculate the center points of both obelisks
         double x1 = pos1.getX() + 0.5;
         double y1 = pos1.getY() + 2; // Middle of the obelisk
         double z1 = pos1.getZ() + 0.5;
         
         double x2 = pos2.getX() + 0.5;
         double y2 = pos2.getY() + 2; // Middle of the obelisk
         double z2 = pos2.getZ() + 0.5;
         
         // Dynamic color generation based on number of obelisks
         Color startColor = generateDynamicColor(connectionIndex, totalObelisks);
         Color endColor = new Color(255, 255, 255); // White end
         
         // Adjust particle count based on number of obelisks
         int particleCount = Math.max(15, 25 - (totalObelisks - 5) * 2); // More obelisks = fewer particles per line
         
         // Create dynamic pentagram connection particles
         for (int i = 0; i < particleCount; i++) {
             double progress = (time * 0.08 + i * (1.0 / particleCount) + connectionIndex * 0.2) % 1.0;
             
             // Calculate position along the connection line
             double particleX = x1 + (x2 - x1) * progress;
             double particleY = y1 + (y2 - y1) * progress;
             double particleZ = z1 + (z2 - z1) * progress;
             
             // Straight line - no wave or spiral motion
             
             WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                     .setScaleData(GenericParticleData.create(0.1f, 0).build())
                     .setTransparencyData(GenericParticleData.create(0.9f, 0.1f).build())
                     .setColorData(ColorParticleData.create(startColor, endColor).setCoefficient(1.5f).setEasing(Easing.BOUNCE_IN_OUT).build())
                     .setSpinData(SpinParticleData.create(0.6f, 1.2f).setSpinOffset(time + i + connectionIndex).setEasing(Easing.QUARTIC_IN).build())
                     .setLifetime(120)
                     .addMotion(0, 0.02f, 0)
                     .enableNoClip()
                     .spawn(world, particleX, particleY, particleZ);
         }
     }
     
     private static Color generateDynamicColor(int connectionIndex, int totalObelisks) {
         // Generate colors based on the number of obelisks
         // For 5 obelisks: classic rainbow colors
         // For more obelisks: interpolate between colors
         
         Color[] baseColors = {
             new Color(255, 0, 0),    // Red
             new Color(255, 165, 0),  // Orange
             new Color(255, 255, 0),  // Yellow
             new Color(0, 255, 0),    // Green
             new Color(0, 0, 255),    // Blue
             new Color(128, 0, 128),  // Purple
             new Color(255, 0, 255),  // Magenta
             new Color(0, 255, 255),  // Cyan
             new Color(255, 192, 203), // Pink
             new Color(255, 215, 0)   // Gold
         };
         
         if (totalObelisks <= baseColors.length) {
             return baseColors[connectionIndex % baseColors.length];
         } else {
             // Interpolate between colors for larger numbers
             float hue = (connectionIndex * 360.0f / totalObelisks) % 360.0f;
             return Color.getHSBColor(hue / 360.0f, 0.8f, 1.0f);
         }
     }
}