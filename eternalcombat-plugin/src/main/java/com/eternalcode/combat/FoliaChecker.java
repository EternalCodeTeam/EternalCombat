package com.eternalcode.combat;

import org.bukkit.plugin.Plugin;

public final class FoliaChecker {

    private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
    private static Boolean cachedResult = null;

    private FoliaChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isFolia() {
        if (cachedResult == null) {
            try {
                Class.forName(FOLIA_CLASS);
                cachedResult = true;
            }
            catch (ClassNotFoundException exception) {
                cachedResult = false;
            }
        }
        return cachedResult;
    }

    public static boolean isFolia(Plugin plugin) {
        if (cachedResult == null) {
            try {
                Class.forName(FOLIA_CLASS);
                cachedResult = true;
                plugin.getLogger().info("» Detected Folia environment.");
            }
            catch (ClassNotFoundException exception) {
                cachedResult = false;
                plugin.getLogger().info("» Detected Bukkit/Paper environment.");
            }
        }

        return cachedResult;
    }

    public static void clearCache() {
        cachedResult = null;
    }
}
