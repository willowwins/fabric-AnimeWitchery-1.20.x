package net.willowins.animewitchery.config;

import net.fabricmc.loader.api.FabricLoader;
import net.willowins.animewitchery.AnimeWitchery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configuration for the auto-load menu world system
 * Simple text file format - just the world name from your saves folder
 */
public class MenuWorldConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
            .resolve("animewitchery_menu_world.txt");

    private static String menuWorldName = null;
    private static boolean loaded = false;

    /**
     * Load the config file
     * Returns the world name to auto-load, or null if disabled/not configured
     */
    public static String getMenuWorldName() {
        if (!loaded) {
            load();
        }
        return menuWorldName;
    }

    /**
     * Check if auto-load is enabled
     */
    public static boolean isEnabled() {
        return getMenuWorldName() != null && !getMenuWorldName().trim().isEmpty();
    }

    /**
     * Load or create the config file
     */
    private static void load() {
        loaded = true;

        AnimeWitchery.LOGGER.info("[MenuWorld] Loading config from: {}", CONFIG_PATH.toAbsolutePath());
        AnimeWitchery.LOGGER.info("[MenuWorld] Config exists: {}", Files.exists(CONFIG_PATH));

        if (Files.exists(CONFIG_PATH)) {
            try {
                String content = Files.readString(CONFIG_PATH);
                AnimeWitchery.LOGGER.info("[MenuWorld] Config file raw content: '{}'", content);

                // Normalize line endings (handle Windows \r\n)
                content = content.replace("\r\n", "\n").replace("\r", "\n").trim();
                AnimeWitchery.LOGGER.info("[MenuWorld] Config file normalized length: {}", content.length());

                // Ignore comments and empty lines
                String[] lines = content.split("\n");
                for (String line : lines) {
                    String originalLine = line;
                    line = line.trim();
                    AnimeWitchery.LOGGER.info("[MenuWorld] Reading line: '{}' (trimmed: '{}')", originalLine, line);
                    // Skip comments (lines starting with #) and empty lines
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        menuWorldName = line;
                        AnimeWitchery.LOGGER.info("[MenuWorld] ✅ Auto-load enabled for world: '{}' (length: {})",
                                menuWorldName, menuWorldName.length());
                        break;
                    }
                }

                if (menuWorldName == null) {
                    AnimeWitchery.LOGGER
                            .info("[MenuWorld] Config file exists but is empty or commented out - auto-load disabled");
                }
            } catch (Exception e) {
                AnimeWitchery.LOGGER.error("[MenuWorld] Failed to load config", e);
                menuWorldName = null;
            }
        } else {
            // Create default config file with instructions
            createDefaultConfig();
        }
    }

    /**
     * Create the default config file with instructions
     */
    private static void createDefaultConfig() {
        try {
            String defaultContent = "# AnimeWitchery Menu World Auto-Load Config\n" +
                    "#\n" +
                    "# The menu world will be automatically installed on first launch\n" +
                    "# with cheats enabled, adventure mode, and locked midnight time\n" +
                    "#\n" +
                    "# To ENABLE auto-load: Uncomment the line below (remove the #)\n" +
                    "#\n" +
                    "# animewitchery_menu\n";

            Files.writeString(CONFIG_PATH, defaultContent);
            AnimeWitchery.LOGGER.info("[MenuWorld] Created default config at {}", CONFIG_PATH);
            // Re-parse to get the enabled value
            load();
        } catch (IOException e) {
            AnimeWitchery.LOGGER.error("[MenuWorld] Failed to create default config", e);
            menuWorldName = null;
        }
    }
}
