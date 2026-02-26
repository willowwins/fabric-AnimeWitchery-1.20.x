package net.willowins.animewitchery.component;

import java.util.ArrayList;
import java.util.List;

public class ClassRegistry {
    public static final List<String> CLASSES = new ArrayList<>();

    static {
        // Combat
        CLASSES.add("Paladin");
        CLASSES.add("Knight");
        CLASSES.add("Brute");
        CLASSES.add("Assassin");
        CLASSES.add("Monk");
        CLASSES.add("Butcher");
        CLASSES.add("Deathbringer");
        CLASSES.add("Haloic");

        // Magic
        CLASSES.add("Mage");
        CLASSES.add("Summoner");
        CLASSES.add("Healer");
        CLASSES.add("Alchemist");
        CLASSES.add("Sanguine");
        CLASSES.add("Druid");
        CLASSES.add("Obelisk");

        // Tech/Utility
        CLASSES.add("Engineer"); // Railgunner
        CLASSES.add("Gunslinger");
        CLASSES.add("Gambler");

        // Civilian
        CLASSES.add("Farmer");
        CLASSES.add("Prospector");
        CLASSES.add("Hoarder");
        CLASSES.add("Civilian"); // Femboy

        // Special
        CLASSES.add("Resonant");
    }

    public static boolean isValidClass(String className) {
        return CLASSES.contains(className);
    }
}
