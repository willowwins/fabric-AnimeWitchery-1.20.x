package net.willowins.animewitchery.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import java.util.HashSet;
import java.util.Set;
import net.willowins.animewitchery.mana.ModComponents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import java.util.UUID;

public class ClassComponent implements IClassComponent, AutoSyncedComponent {
    private final PlayerEntity player;

    private String primaryClass = "";
    private String secondaryClass = "";
    private int level = 1;
    private int experience = 0;
    private int skillPoints = 0;
    private final Set<String> unlockedSkills = new HashSet<>();

    public ClassComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public String getPrimaryClass() {
        return primaryClass;
    }

    @Override
    public void setPrimaryClass(String className) {
        this.primaryClass = className;
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public String getSecondaryClass() {
        return secondaryClass;
    }

    @Override
    public void setSecondaryClass(String className) {
        this.secondaryClass = className;
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public boolean hasSecondaryClass() {
        return !secondaryClass.isEmpty();
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(100, level));
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public void addLevel(int levels) {
        setLevel(this.level + levels);
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public void setExperience(int xp) {
        this.experience = Math.max(0, xp);
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public void addExperience(int xp) {
        setExperience(this.experience + xp);
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public void addSkillPoints(int points) {
        this.skillPoints += points;
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public void consumeSkillPoints(int points) {
        this.skillPoints = Math.max(0, this.skillPoints - points);
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public boolean isSkillUnlocked(String skillId) {
        return unlockedSkills.contains(skillId);
    }

    @Override
    public void unlockSkill(String skillId) {
        if (unlockedSkills.add(skillId)) {
            applySkillModifiers();
            ModComponents.CLASS_DATA.sync(player);
        }
    }

    @Override
    public Set<String> getUnlockedSkills() {
        return new HashSet<>(unlockedSkills);
    }

    @Override
    public int getManaBonus() {
        int bonus = 0;
        // Core Mana 1-10
        for (int i = 1; i <= 10; i++) {
            if (isSkillUnlocked("core_mana_" + i)) {
                bonus += 10;
            }
        }
        return bonus;
    }

    private void applySkillModifiers() {
        if (player.getWorld().isClient)
            return;

        // --- Core Health (1-10) ---
        int healthBonus = 0;
        for (int i = 1; i <= 10; i++) {
            if (isSkillUnlocked("core_health_" + i))
                healthBonus += 2;
        }

        updateAttributeModifier(
                EntityAttributes.GENERIC_MAX_HEALTH,
                "7a5abc12-3456-7890-abcd-ef1234567890",
                "Skill Health Bonus",
                healthBonus,
                EntityAttributeModifier.Operation.ADDITION,
                healthBonus > 0);

        // --- Core Strength (1-10) ---
        int damageBonus = 0;
        for (int i = 1; i <= 10; i++) {
            if (isSkillUnlocked("core_damage_" + i))
                damageBonus += 1;
        }

        updateAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                "8b6bcd23-4567-8901-bcde-f01234567891",
                "Skill Damage Bonus",
                damageBonus,
                EntityAttributeModifier.Operation.ADDITION,
                damageBonus > 0);

        // --- Core Speed (1-10) ---
        double speedBonus = 0.0;
        for (int i = 1; i <= 10; i++) {
            if (isSkillUnlocked("core_speed_" + i))
                speedBonus += 0.02; // 2% per tier
        }

        updateAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "9c7cde34-5678-9012-cdef-012345678902",
                "Skill Speed Bonus",
                speedBonus,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL,
                speedBonus > 0);

        // Refresh Health if needed (to prevent "ghost" missing health)
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void updateAttributeModifier(EntityAttribute attribute, String uuid,
            String name, double value, EntityAttributeModifier.Operation operation,
            boolean enable) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null)
            return;

        UUID modifierId = UUID.fromString(uuid);
        EntityAttributeModifier modifier = instance.getModifier(modifierId);

        if (enable) {
            if (modifier == null) {
                modifier = new EntityAttributeModifier(modifierId, name, value,
                        operation);
                instance.addPersistentModifier(modifier);
            }
        } else {
            if (modifier != null) {
                instance.removeModifier(modifierId);
            }
        }
    }

    @Override
    public void sync() {
        ModComponents.CLASS_DATA.sync(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        try {
            primaryClass = tag.getString("PrimaryClass");
            secondaryClass = tag.getString("SecondaryClass");
            level = tag.getInt("Level");
            if (level < 1)
                level = 1;
            experience = tag.getInt("Experience");
            skillPoints = tag.getInt("SkillPoints");

            unlockedSkills.clear();
            NbtList list = tag.getList("UnlockedSkills", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                unlockedSkills.add(list.getString(i));
            }

            // Re-apply modifiers after loading
            applySkillModifiers();

        } catch (Exception e) {
            net.willowins.animewitchery.AnimeWitchery.LOGGER.error("Failed to read Class Component NBT", e);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString("PrimaryClass", primaryClass);
        tag.putString("SecondaryClass", secondaryClass);
        tag.putInt("Level", level);
        tag.putInt("Experience", experience);
        tag.putInt("SkillPoints", skillPoints);

        NbtList list = new NbtList();
        for (String skill : unlockedSkills) {
            list.add(NbtString.of(skill));
        }
        tag.put("UnlockedSkills", list);
    }
}
