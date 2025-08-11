package net.willowins.animewitchery.ritual;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.willowins.animewitchery.block.ModBlocks;
import net.willowins.animewitchery.block.entity.ObeliskBlockEntity;
import net.willowins.animewitchery.block.entity.ActiveObeliskBlockEntity;
import java.util.Map;
import java.util.HashMap;

public class RitualConfigurationBuilder {
    
    /**
     * Builds a ritual configuration based on the obelisks around a barrier circle
     */
    public static RitualConfiguration buildConfiguration(World world, BlockPos circlePos) {
        Map<BlockPos, Integer> obeliskVariants = new HashMap<>();
        Map<Direction, Integer> directionVariants = new HashMap<>();
        
        // Find obelisks in each direction
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;
            
            BlockPos obeliskPos = circlePos.offset(direction, 5);
            if (world.getBlockState(obeliskPos).isOf(ModBlocks.ACTIVE_OBELISK)) {
                BlockEntity blockEntity = world.getBlockEntity(obeliskPos);
                if (blockEntity instanceof ActiveObeliskBlockEntity activeObelisk) {
                    int variant = activeObelisk.getTextureVariant();
                    obeliskVariants.put(obeliskPos, variant);
                    directionVariants.put(direction, variant);
                }
            }
        }
        
        // Determine ritual type from North obelisk (variant 4 = shield rune = barrier mode, variant 5 = effect rune = effect mode)
        RitualConfiguration.RitualType ritualType = null; // Will be set based on North obelisk
        if (directionVariants.containsKey(Direction.NORTH)) {
            int northVariant = directionVariants.get(Direction.NORTH);
            if (northVariant == 4) {
                ritualType = RitualConfiguration.RitualType.BARRIER;
            } else if (northVariant == 7) {
                ritualType = RitualConfiguration.RitualType.EFFECT;
            } else if (northVariant == 0) {
                // Blank obelisk - invalid ritual type
                ritualType = null; // This will cause validation to fail
            }
        }
        
        // Determine barrier shape or effect type from East obelisk
        RitualConfiguration.BarrierShape barrierShape = null; // Will be set based on ritual type
        RitualConfiguration.EffectType effectType = null; // Will be set based on ritual type
        
        if (directionVariants.containsKey(Direction.EAST)) {
            int eastVariant = directionVariants.get(Direction.EAST);
            if (ritualType == RitualConfiguration.RitualType.BARRIER) {
                // Barrier mode: East obelisk defines barrier shape
                if (eastVariant == 1) {
                    barrierShape = RitualConfiguration.BarrierShape.SQUARE;
                } else if (eastVariant == 2) {
                    barrierShape = RitualConfiguration.BarrierShape.CIRCULAR;
                } else if (eastVariant == 0) {
                    // Blank obelisk - invalid barrier shape
                    barrierShape = null;
                }
                // Variant 3 is invalid for barrier shape
            } else if (ritualType == RitualConfiguration.RitualType.EFFECT) {
                // Effect mode: East obelisk defines effect type
                if (eastVariant == 5) {
                    effectType = RitualConfiguration.EffectType.POISON;
                } else if (eastVariant == 6) {
                    effectType = RitualConfiguration.EffectType.REGENERATION;
                } else if (eastVariant == 0) {
                    // Blank obelisk - invalid effect type
                    effectType = null;
                }
                // Other variants are invalid for effect type
            }
        }
        
        // Get strength from South obelisk (variants 1-3)
        int strength = 0; // Invalid by default
        if (directionVariants.containsKey(Direction.SOUTH)) {
            int southVariant = directionVariants.get(Direction.SOUTH);
            if (southVariant >= 1 && southVariant <= 3) {
                strength = southVariant;
            }
        }
        
        // Get regen rate from West obelisk (variants 1-3)
        int regenRate = 0; // Invalid by default
        if (directionVariants.containsKey(Direction.WEST)) {
            int westVariant = directionVariants.get(Direction.WEST);
            if (westVariant >= 1 && westVariant <= 3) {
                regenRate = westVariant;
            }
        }
        
        System.out.println("RitualConfigurationBuilder: Detected variants - N:" + directionVariants.getOrDefault(Direction.NORTH, -1) + 
                          " E:" + directionVariants.getOrDefault(Direction.EAST, -1) + 
                          " S:" + directionVariants.getOrDefault(Direction.SOUTH, -1) + 
                          " W:" + directionVariants.getOrDefault(Direction.WEST, -1));
        
        if (ritualType == RitualConfiguration.RitualType.BARRIER) {
            System.out.println("RitualConfigurationBuilder: Final config - type:" + ritualType + " shape:" + barrierShape + " strength:" + strength + " regen:" + regenRate);
        } else if (ritualType == RitualConfiguration.RitualType.EFFECT) {
            System.out.println("RitualConfigurationBuilder: Final config - type:" + ritualType + " effect:" + effectType + " strength:" + strength + " duration:" + regenRate);
        }
        
        // Check if we have invalid ritual type or missing required fields
        if (ritualType == null) {
            System.out.println("RitualConfigurationBuilder: Invalid configuration - null ritual type");
            return new RitualConfiguration(); // Return invalid configuration
        }
        
        if (ritualType == RitualConfiguration.RitualType.BARRIER && barrierShape == null) {
            System.out.println("RitualConfigurationBuilder: Invalid configuration - null barrier shape for barrier mode");
            return new RitualConfiguration(); // Return invalid configuration
        }
        
        if (ritualType == RitualConfiguration.RitualType.EFFECT && effectType == null) {
            System.out.println("RitualConfigurationBuilder: Invalid configuration - null effect type for effect mode");
            return new RitualConfiguration(); // Return invalid configuration
        }
        
        return new RitualConfiguration(ritualType, barrierShape, effectType, strength, regenRate, obeliskVariants);
    }
    
    /**
     * Validates if a ritual configuration is complete and valid
     */
    public static boolean isValidConfiguration(RitualConfiguration config) {
        if (!config.isValid()) return false;
        
        // Check if we have valid obelisk variants (not blank obelisks)
        if (!config.hasValidObeliskVariants()) {
            return false;
        }
        
        // Accept both BARRIER and EFFECT ritual types
        return config.getRitualType() == RitualConfiguration.RitualType.BARRIER || 
               config.getRitualType() == RitualConfiguration.RitualType.EFFECT;
    }
}
