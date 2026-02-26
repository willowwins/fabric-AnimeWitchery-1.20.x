package net.willowins.animewitchery.component;

import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {
    public static class SkillDef {
        public String id;
        public String name;
        public String description;
        public int cost;
        public String parentId; // Null if root
        public int x;
        public int y;
        public int tier; // New field for Tier Gating

        public SkillDef(String id, String name, String description, int cost, String parentId, int x, int y, int tier) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.cost = cost;
            this.parentId = parentId;
            this.x = x;
            this.y = y;
            this.tier = tier;
        }
    }

    public static final Map<String, SkillDef> SKILLS = new HashMap<>();

    public static void register(SkillDef skill) {
        SKILLS.put(skill.id, skill);
    }

    static {
        // --- Core Skills (Tiers 1-10) ---
        // Vertical Layout:
        // Health (Left x=-90), Strength (Center-Left x=-30)
        // Speed (Center-Right x=30), Mana (Right x=90)
        // Y starts at 0 (Tier 1) and goes UP (negative Y)

        int startY = 0;
        int tierHeight = 50; // Distance between tiers

        for (int i = 1; i <= 10; i++) {
            int y = startY - ((i - 1) * tierHeight);
            String suffix = "_" + i;
            String roman = toRoman(i);

            // Health (Column 1)
            String hParent = (i == 1) ? null : "core_health_" + (i - 1);
            register(new SkillDef("core_health" + suffix, "Health " + roman, "Max Health +2", 1, hParent, -90, y, i));

            // Strength (Column 2)
            String dParent = (i == 1) ? null : "core_damage_" + (i - 1);
            register(new SkillDef("core_damage" + suffix, "Strength " + roman, "Damage +1", 1, dParent, -30, y, i));

            // Speed (Column 3)
            String sParent = (i == 1) ? null : "core_speed_" + (i - 1);
            register(new SkillDef("core_speed" + suffix, "Agility " + roman, "Speed +2%", 1, sParent, 30, y, i));

            // Mana (Column 4)
            String mParent = (i == 1) ? null : "core_mana_" + (i - 1);
            register(new SkillDef("core_mana" + suffix, "Arcane " + roman, "Max Mana +10", 1, mParent, 90, y, i));
        }

        // --- Class Skills (Branching off specific tiers) ---
        // Paladin (Off Health Tier 3) -> Visual: Left of Health
        register(new SkillDef("paladin_shield", "Holy Shield", "Blocking heals you.", 3, "core_health_3", -150, -100,
                3));
        register(new SkillDef("paladin_divine_f favor", "Divine Favor", "Healing +20%", 5, "core_health_5", -150, -200,
                5));
        register(new SkillDef("paladin_smite", "Smite", "Undead Dmg +20%", 5, "core_damage_5", -60, -200, 5)); // Between
                                                                                                               // Health/Str

        // Healer (Off Health/Mana)
        register(new SkillDef("healer_aura", "Healing Aura", "Passive regen.", 3, "core_health_3", -120, -100, 3));
        register(new SkillDef("healer_efficiency", "Mana Eff.", "Cost -10%", 5, "core_mana_5", 120, -200, 5));

        // Deathbringer (Off Strength)
        register(new SkillDef("death_reap", "Soul Reap", "Kill restores Mana.", 3, "core_damage_3", -60, -100, 3));
        register(new SkillDef("death_decay", "Decay Aura", "Wither enemies.", 5, "core_damage_5", -60, -200, 5));

        // Sanguine (Off Health)
        register(
                new SkillDef("sanguine_blood_pact", "Blood Pact", "HP +10, -Mana.", 3, "core_health_3", -150, -120, 3));
        register(new SkillDef("sanguine_thirst", "Vampirism", "Lifesteal.", 5, "core_damage_5", 0, -200, 5));

        // Assassin (Off Speed)
        register(new SkillDef("assassin_backstab", "Backstab", "Back Crit.", 3, "core_speed_3", 60, -100, 3));
        register(new SkillDef("assassin_blur", "Blur", "Dodge.", 5, "core_speed_5", 60, -200, 5));

        // Butcher (Off Strength)
        register(new SkillDef("butcher_chop", "Chop", "Axe Dmg.", 3, "core_damage_3", 0, -100, 3));

        // Brute (Off Strength)
        register(new SkillDef("brute_smash", "Smash", "Knockback.", 3, "core_damage_3", -60, -120, 3));

        // Monk (Off Speed)
        register(new SkillDef("monk_fist", "Iron Fist", "Unarmed Dmg.", 3, "core_damage_3", 0, -120, 3));
        register(new SkillDef("monk_step", "Flash Step", "Speed.", 5, "core_speed_5", 60, -220, 5));

        // Summoner (Off Mana) -> Right of Mana
        register(new SkillDef("summon_horde", "Horde", "+1 Summon.", 3, "core_mana_3", 150, -100, 3));
        register(new SkillDef("summon_bond", "Bond", "Summon HP.", 5, "core_mana_5", 150, -200, 5));

        // Mage (Off Mana)
        register(new SkillDef("mage_mana_regen", "Recovery", "Regen.", 3, "core_mana_3", 120, -120, 3));
        register(new SkillDef("mage_power", "Power", "Magic Dmg.", 5, "core_mana_5", 120, -220, 5));

        // Farmer (Off Speed)
        register(new SkillDef("farmer_harvest", "Harvest", "Crop Yield.", 3, "core_speed_3", 60, -120, 3));

        // Prospector (Off Mana)
        register(new SkillDef("prospector_vision", "Gold Sight", "Ores.", 3, "core_mana_3", 150, -120, 3));

        // Hoarder (Off Speed)
        register(new SkillDef("hoarder_luck", "Luck", "Luck +1", 3, "core_speed_3", 60, -140, 3));

        // Druid (Off Health)
        register(new SkillDef("druid_regrowth", "Regrowth", "Grass Heal.", 3, "core_health_3", -120, -120, 3));

        // Alchemist (Off Mana)
        register(new SkillDef("alchemist_duration", "Potency", "Potion Dur.", 3, "core_mana_3", 150, -140, 3));

        // Gunslinger (Off Speed)
        register(new SkillDef("gunslinger_quickdraw", "Quickdraw", "Atk Spd.", 3, "core_speed_3", 60, -80, 3));

        // Gambler (Off Speed)
        register(new SkillDef("gambler_ante", "Ante", "Random Crit.", 3, "core_speed_3", 60, -60, 3));

        // Femboy (Off Speed)
        register(new SkillDef("femboy_charm", "Charm", "Aggro Range.", 3, "core_speed_3", 60, -40, 3));

        // Knight (Off Health)
        register(new SkillDef("knight_valor", "Valor", "Fear Immune.", 3, "core_health_3", -120, -80, 3));
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    public static SkillDef get(String id) {
        return SKILLS.get(id);
    }
}
