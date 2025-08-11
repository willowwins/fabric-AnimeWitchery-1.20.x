package net.willowins.animewitchery.ritual;

import net.minecraft.util.math.BlockPos;
import java.util.Map;
import java.util.HashMap;

public class RitualConfiguration {
    public enum RitualType {
        BARRIER, EFFECT
    }
    
    public enum BarrierShape {
        SQUARE, CIRCULAR
    }
    
    public enum EffectType {
        POISON, REGENERATION
    }
    
    private final RitualType ritualType; // Can be null if invalid
    private final BarrierShape barrierShape; // Can be null if invalid (for barrier mode)
    private final EffectType effectType; // Can be null if invalid (for effect mode)
    private final int strength; // 1-3 scale
    private final int regenRate; // 1-3 scale
    private final Map<BlockPos, Integer> obeliskVariants;
    
    public RitualConfiguration(RitualType ritualType, BarrierShape barrierShape, EffectType effectType, int strength, int regenRate, Map<BlockPos, Integer> obeliskVariants) {
        this.ritualType = ritualType;
        this.barrierShape = barrierShape;
        this.effectType = effectType;
        this.strength = strength;
        this.regenRate = regenRate;
        this.obeliskVariants = new HashMap<>(obeliskVariants);
    }
    
    /**
     * Constructor for invalid configurations
     */
    public RitualConfiguration() {
        this.ritualType = null;
        this.barrierShape = null;
        this.effectType = null;
        this.strength = 0;
        this.regenRate = 0;
        this.obeliskVariants = new HashMap<>();
    }
    
    // Getters
    public RitualType getRitualType() { return ritualType; }
    public BarrierShape getBarrierShape() { return barrierShape; }
    public EffectType getEffectType() { return effectType; }
    public int getStrength() { return strength; }
    public int getRegenRate() { return regenRate; }
    public Map<BlockPos, Integer> getObeliskVariants() { return obeliskVariants; }
    
    // Validation methods
    public boolean isValid() {
        if (ritualType == null) return false;
        
        if (ritualType == RitualType.BARRIER) {
            return barrierShape != null && strength >= 1 && strength <= 3 && regenRate >= 1 && regenRate <= 3;
        } else if (ritualType == RitualType.EFFECT) {
            return effectType != null && strength >= 1 && strength <= 3 && regenRate >= 1 && regenRate <= 3;
        }
        
        return false;
    }
    
    /**
     * Checks if the configuration has valid obelisk variants for ritual activation
     */
    public boolean hasValidObeliskVariants() {
        if (ritualType == null) return false;
        
        if (ritualType == RitualType.BARRIER) {
            return barrierShape != null && strength > 0 && regenRate > 0;
        } else if (ritualType == RitualType.EFFECT) {
            return effectType != null && strength > 0 && regenRate > 0;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        if (ritualType == RitualType.BARRIER) {
            return String.format("RitualConfiguration{type=%s, shape=%s, strength=%d, regen=%d}", 
                ritualType, barrierShape, strength, regenRate);
        } else if (ritualType == RitualType.EFFECT) {
            return String.format("RitualConfiguration{type=%s, effect=%s, strength=%d, duration=%d}", 
                ritualType, effectType, strength, regenRate);
        }
        return String.format("RitualConfiguration{type=%s, invalid}", ritualType);
    }
}
