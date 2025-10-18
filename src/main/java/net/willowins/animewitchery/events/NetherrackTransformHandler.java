package net.willowins.animewitchery.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.entity.GrowthAcceleratorBlockEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NetherrackTransformHandler {
    
    // Store both position and world key
    private static class WorldBlockPos {
        final RegistryKey<World> worldKey;
        final BlockPos pos;
        
        WorldBlockPos(RegistryKey<World> worldKey, BlockPos pos) {
            this.worldKey = worldKey;
            this.pos = pos;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WorldBlockPos)) return false;
            WorldBlockPos that = (WorldBlockPos) o;
            return worldKey.equals(that.worldKey) && pos.equals(that.pos);
        }
        
        @Override
        public int hashCode() {
            return 31 * worldKey.hashCode() + pos.hashCode();
        }
    }
    
    private static final Map<WorldBlockPos, Long> transformingBlocks = new HashMap<>();
    private static final long TRANSFORM_TIME = 6000; // 5 minutes = 6000 ticks (20 ticks per second)
    private static final int GROWTH_ACCELERATOR_RADIUS = 3; // Same as growth accelerator's radius
    
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(NetherrackTransformHandler::tick);
    }
    
    private static void tick(ServerWorld world) {
        // Process in all dimensions (not just Nether)
        // This allows netherrack transformation to work in Paradise Lost and other dimensions
        
        RegistryKey<World> currentWorldKey = world.getRegistryKey();
        
        Iterator<Map.Entry<WorldBlockPos, Long>> iterator = transformingBlocks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<WorldBlockPos, Long> entry = iterator.next();
            WorldBlockPos worldBlockPos = entry.getKey();
            long startTime = entry.getValue();
            
            // Only process blocks in this world
            if (!worldBlockPos.worldKey.equals(currentWorldKey)) {
                continue;
            }
            
            BlockPos pos = worldBlockPos.pos;
            
            // Check if block is still valid
            if (!world.getBlockState(pos).isOf(Blocks.NETHERRACK)) {
                iterator.remove();
                continue;
            }
            
            // Check if still has 4 adjacent ancient debris
            if (!hasFourAdjacentAncientDebris(world, pos)) {
                iterator.remove();
                continue;
            }
            
            // Calculate time required based on nearby growth accelerators
            long currentTime = world.getTime();
            long timeElapsed = currentTime - startTime;
            
            // Check for nearby active growth accelerators
            int acceleratorCount = countNearbyActiveAccelerators(world, pos);
            
            // Calculate effective time multiplier
            // Each accelerator reduces time by 50% (multiplies speed by 2x)
            // Multiple accelerators stack: 1 = 2x, 2 = 4x, 3 = 8x, etc.
            long effectiveTimeRequired = TRANSFORM_TIME >> acceleratorCount; // Bit shift for power of 2
            
            if (timeElapsed >= effectiveTimeRequired) {
                // Transform to ancient debris
                world.setBlockState(pos, Blocks.ANCIENT_DEBRIS.getDefaultState());
                iterator.remove();
            } else {
                // Spawn black particles to show transformation is in progress
                // Spawn particles every 20 ticks (1 second) to show progress
                if (world.getTime() % 20 == 0) {
                    System.out.println("[Netherrack Transform] Progress: " + timeElapsed + "/" + effectiveTimeRequired + " at " + pos);
                    world.spawnParticles(
                        ParticleTypes.SMOKE,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        3,
                        0.2, 0.1, 0.2,
                        0.01
                    );
                }
            }
        }
        
        // Every 20 ticks (1 second), scan for new valid netherrack blocks
        if (world.getTime() % 20 == 0) {
            scanForValidNetherrack(world);
        }
    }
    
    private static void scanForValidNetherrack(ServerWorld world) {
        // Get loaded chunks and scan for netherrack with 4 adjacent
        RegistryKey<World> worldKey = world.getRegistryKey();
        
        world.getPlayers().forEach(player -> {
            BlockPos playerPos = player.getBlockPos();
            int scanRadius = 16; // Scan 16 blocks around each player
            
            for (int x = -scanRadius; x <= scanRadius; x++) {
                for (int y = -scanRadius; y <= scanRadius; y++) {
                    for (int z = -scanRadius; z <= scanRadius; z++) {
                        BlockPos checkPos = playerPos.add(x, y, z);
                        WorldBlockPos worldBlockPos = new WorldBlockPos(worldKey, checkPos);
                        
                        // Skip if already being tracked
                        if (transformingBlocks.containsKey(worldBlockPos)) {
                            continue;
                        }
                        
                        // Check if it's netherrack with 4 adjacent ancient debris
                        if (world.getBlockState(checkPos).isOf(Blocks.NETHERRACK) 
                                && hasFourAdjacentAncientDebris(world, checkPos)) {
                            transformingBlocks.put(worldBlockPos, world.getTime());
                            System.out.println("[Netherrack Transform] Found valid netherrack at " + checkPos + " in world " + worldKey.getValue());
                        }
                    }
                }
            }
        });
    }
    
    private static boolean hasFourAdjacentAncientDebris(World world, BlockPos pos) {
        int count = 0;
        
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.offset(direction);
            if (world.getBlockState(adjacentPos).isOf(Blocks.ANCIENT_DEBRIS)) {
                count++;
            }
        }
        
        return count >= 4;
    }
    
    /**
     * Counts the number of active (fueled) growth accelerators within range
     */
    private static int countNearbyActiveAccelerators(ServerWorld world, BlockPos pos) {
        int count = 0;
        
        // Check within the growth accelerator's effective radius
        for (int dx = -GROWTH_ACCELERATOR_RADIUS; dx <= GROWTH_ACCELERATOR_RADIUS; dx++) {
            for (int dy = -1; dy <= 1; dy++) { // Growth accelerator checks -1 to +1 in Y
                for (int dz = -GROWTH_ACCELERATOR_RADIUS; dz <= GROWTH_ACCELERATOR_RADIUS; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    BlockEntity blockEntity = world.getBlockEntity(checkPos);
                    
                    // Check if it's a growth accelerator with fuel
                    if (blockEntity instanceof GrowthAcceleratorBlockEntity accelerator) {
                        if (accelerator.getFuelTime() > 0) {
                            count++;
                        }
                    }
                }
            }
        }
        
        return count;
    }
}


