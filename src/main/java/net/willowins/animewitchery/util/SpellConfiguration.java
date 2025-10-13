package net.willowins.animewitchery.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single named spell configuration within a spellbook
 */
public class SpellConfiguration {
    private String name;
    private List<String> spells;
    private String pattern;
    private int delay;
    
    public SpellConfiguration(String name, List<String> spells, String pattern, int delay) {
        this.name = name;
        this.spells = new ArrayList<>(spells);
        this.pattern = pattern;
        this.delay = delay;
    }
    
    /**
     * Creates a configuration from NBT
     */
    public static SpellConfiguration fromNbt(NbtCompound nbt) {
        String name = nbt.getString("name");
        String pattern = nbt.getString("pattern");
        int delay = nbt.getInt("delay");
        
        List<String> spells = new ArrayList<>();
        NbtList spellList = nbt.getList("spells", 8);
        for (int i = 0; i < spellList.size(); i++) {
            spells.add(spellList.getString(i));
        }
        
        return new SpellConfiguration(name, spells, pattern, delay);
    }
    
    /**
     * Saves this configuration to NBT
     */
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putString("pattern", pattern);
        nbt.putInt("delay", delay);
        
        NbtList spellList = new NbtList();
        for (String spell : spells) {
            spellList.add(NbtString.of(spell));
        }
        nbt.put("spells", spellList);
        
        return nbt;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getSpells() {
        return new ArrayList<>(spells);
    }
    
    public void setSpells(List<String> spells) {
        this.spells = new ArrayList<>(spells);
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public int getDelay() {
        return delay;
    }
    
    public void setDelay(int delay) {
        this.delay = delay;
    }
    
    public int getSpellCount() {
        return spells.size();
    }
}

