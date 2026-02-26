package net.willowins.animewitchery.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.Set;

public interface IClassComponent extends ComponentV3 {
    // Class Management
    String getPrimaryClass();

    void setPrimaryClass(String className);

    String getSecondaryClass();

    void setSecondaryClass(String className);

    boolean hasSecondaryClass();

    // Leveling
    int getLevel();

    void setLevel(int level);

    void addLevel(int levels);

    int getExperience();

    void setExperience(int xp);

    void addExperience(int xp);

    int getSkillPoints();

    void addSkillPoints(int points);

    void consumeSkillPoints(int points);

    // Skills
    boolean isSkillUnlocked(String skillId);

    void unlockSkill(String skillId);

    Set<String> getUnlockedSkills();

    // Bonuses
    int getManaBonus();

    // Sync
    void sync();
}
