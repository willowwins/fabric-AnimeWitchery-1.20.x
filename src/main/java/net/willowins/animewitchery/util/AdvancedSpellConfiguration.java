package net.willowins.animewitchery.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Advanced spell configuration with individual spell positioning, targeting, delays, and multiplicities
 */
public class AdvancedSpellConfiguration {
    private String name;
    private List<SpellEntry> spells;
    private int globalDelay;
    
    public AdvancedSpellConfiguration(String name) {
        this.name = name;
        this.spells = new ArrayList<>();
        this.globalDelay = 0;
    }
    
    public AdvancedSpellConfiguration(String name, List<SpellEntry> spells, int globalDelay) {
        this.name = name;
        this.spells = spells != null ? spells : new ArrayList<>();
        this.globalDelay = globalDelay;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<SpellEntry> getSpells() { return spells; }
    public void setSpells(List<SpellEntry> spells) { this.spells = spells; }
    
    public int getGlobalDelay() { return globalDelay; }
    public void setGlobalDelay(int globalDelay) { this.globalDelay = globalDelay; }
    
    public int getSpellCount() { return spells.size(); }
    
    /**
     * Add a spell to the configuration
     */
    public void addSpell(SpellEntry spell) {
        spells.add(spell);
    }
    
    /**
     * Remove a spell from the configuration by index
     */
    public void removeSpell(int index) {
        if (index >= 0 && index < spells.size()) {
            spells.remove(index);
        }
    }
    
    /**
     * Individual spell entry with advanced configuration
     */
    public static class SpellEntry {
        private String spellName;
        private SpellPosition position;
        private SpellTargeting targeting;
        private int delay;
        private int multiplicity;
        private Vec3d offset;
        private float yawOffset;
        private float pitchOffset;
        
        public SpellEntry(String spellName) {
            this.spellName = spellName;
            this.position = SpellPosition.SELF;
            this.targeting = SpellTargeting.CASTER;
            this.delay = 0;
            this.multiplicity = 1;
            this.offset = Vec3d.ZERO;
            this.yawOffset = 0;
            this.pitchOffset = 0;
        }
        
        public SpellEntry(String spellName, SpellPosition position, SpellTargeting targeting, 
                         int delay, int multiplicity, Vec3d offset, float yawOffset, float pitchOffset) {
            this.spellName = spellName;
            this.position = position;
            this.targeting = targeting;
            this.delay = delay;
            this.multiplicity = multiplicity;
            this.offset = offset;
            this.yawOffset = yawOffset;
            this.pitchOffset = pitchOffset;
        }
        
        // Getters and setters
        public String getSpellName() { return spellName; }
        public void setSpellName(String spellName) { this.spellName = spellName; }
        
        public SpellPosition getPosition() { return position; }
        public void setPosition(SpellPosition position) { this.position = position; }
        
        public SpellTargeting getTargeting() { return targeting; }
        public void setTargeting(SpellTargeting targeting) { this.targeting = targeting; }
        
        public int getDelay() { return delay; }
        public void setDelay(int delay) { this.delay = delay; }
        
        public int getMultiplicity() { return multiplicity; }
        public void setMultiplicity(int multiplicity) { this.multiplicity = multiplicity; }
        
        public Vec3d getOffset() { return offset; }
        public void setOffset(Vec3d offset) { this.offset = offset; }
        
        public float getYawOffset() { return yawOffset; }
        public void setYawOffset(float yawOffset) { this.yawOffset = yawOffset; }
        
        public float getPitchOffset() { return pitchOffset; }
        public void setPitchOffset(float pitchOffset) { this.pitchOffset = pitchOffset; }
        
        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("spell_name", spellName);
            nbt.putString("position", position.name());
            nbt.putString("targeting", targeting.name());
            nbt.putInt("delay", delay);
            nbt.putInt("multiplicity", multiplicity);
            nbt.putDouble("offset_x", offset.x);
            nbt.putDouble("offset_y", offset.y);
            nbt.putDouble("offset_z", offset.z);
            nbt.putFloat("yaw_offset", yawOffset);
            nbt.putFloat("pitch_offset", pitchOffset);
            return nbt;
        }
        
        public static SpellEntry fromNbt(NbtCompound nbt) {
            String spellName = nbt.getString("spell_name");
            SpellPosition position = SpellPosition.valueOf(nbt.getString("position"));
            SpellTargeting targeting = SpellTargeting.valueOf(nbt.getString("targeting"));
            int delay = nbt.getInt("delay");
            int multiplicity = nbt.getInt("multiplicity");
            Vec3d offset = new Vec3d(nbt.getDouble("offset_x"), nbt.getDouble("offset_y"), nbt.getDouble("offset_z"));
            float yawOffset = nbt.getFloat("yaw_offset");
            float pitchOffset = nbt.getFloat("pitch_offset");
            
            return new SpellEntry(spellName, position, targeting, delay, multiplicity, offset, yawOffset, pitchOffset);
        }
    }
    
    /**
     * Spell positioning relative to caster or target
     */
    public enum SpellPosition {
        SELF("Self", "Cast on yourself"),
        FRONT("Front", "Cast in front of caster"),
        BACK("Back", "Cast behind caster"),
        LEFT("Left", "Cast to the left of caster"),
        RIGHT("Right", "Cast to the right of caster"),
        UP("Above", "Cast above caster"),
        DOWN("Below", "Cast below caster"),
        TARGET("Target", "Cast at target position"),
        TARGET_FRONT("Target Front", "Cast in front of target"),
        TARGET_BACK("Target Back", "Cast behind target"),
        TARGET_LEFT("Target Left", "Cast to left of target"),
        TARGET_RIGHT("Target Right", "Cast to right of target"),
        TARGET_UP("Target Above", "Cast above target"),
        TARGET_DOWN("Target Below", "Cast below target"),
        CUSTOM("Custom", "Use custom offset");
        
        private final String displayName;
        private final String description;
        
        SpellPosition(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Spell targeting system
     */
    public enum SpellTargeting {
        CASTER("Caster", "Target the caster"),
        TARGET_ENTITY("Target Entity", "Target the aimed entity"),
        TARGET_BLOCK("Target Block", "Target the aimed block"),
        AUTO("Auto", "Automatically choose best target"),
        AREA("Area", "Area effect at position"),
        PROJECTILE("Projectile", "Projectile from position");
        
        private final String displayName;
        private final String description;
        
        SpellTargeting(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putInt("global_delay", globalDelay);
        
        NbtList spellList = new NbtList();
        for (SpellEntry spell : spells) {
            spellList.add(spell.toNbt());
        }
        nbt.put("spells", spellList);
        
        return nbt;
    }
    
    public static AdvancedSpellConfiguration fromNbt(NbtCompound nbt) {
        String name = nbt.getString("name");
        int globalDelay = nbt.getInt("global_delay");
        
        List<SpellEntry> spells = new ArrayList<>();
        NbtList spellList = nbt.getList("spells", 10);
        for (int i = 0; i < spellList.size(); i++) {
            spells.add(SpellEntry.fromNbt(spellList.getCompound(i)));
        }
        
        return new AdvancedSpellConfiguration(name, spells, globalDelay);
    }
    
    /**
     * Calculate total mana cost for this configuration
     */
    public int calculateTotalManaCost() {
        int totalCost = 0;
        
        for (SpellEntry spell : spells) {
            int baseCost = getSpellBaseCost(spell.getSpellName());
            int spellCost = (int) (baseCost * Math.pow(1.2, spell.getMultiplicity() - 1)); // 20% increase per extra instance
            totalCost += spellCost;
        }
        
        // Add complexity cost based on number of spells and their configurations
        int complexityCost = spells.size() * 50;
        totalCost += complexityCost;
        
        return totalCost;
    }
    
    private int getSpellBaseCost(String spellName) {
        return switch (spellName) {
            case "Fire Blast", "Earth Spike", "Wither Touch", "Light Burst", "Shadow Bind" -> 500;
            case "Water Shield", "Wind Gust" -> 300;
            case "Healing Wave" -> 800;
            default -> 0;
        };
    }
}
