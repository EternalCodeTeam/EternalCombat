package com.eternalcode.combat;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public final class FoliaChecker {

    private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
    private static Boolean cachedResult = null;

    private FoliaChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isFolia() {
        return detectFoliaEnvironment(null);
    }

    public static boolean isFolia(Plugin plugin) {
        return detectFoliaEnvironment(plugin);
    }

    private static synchronized boolean detectFoliaEnvironment(@Nullable Plugin plugin) {
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            Class.forName(FOLIA_CLASS);
            cachedResult = true;
            if (plugin != null) {
                plugin.getLogger().info("» Detected Folia environment.");
            }
        }
        catch (ClassNotFoundException exception) {
            cachedResult = false;
            if (plugin != null) {
                plugin.getLogger().info("» Detected Bukkit/Paper environment.");
            }
        }
        return cachedResult;
    }

    public static void clearCache() {
        cachedResult = null;
    }
}
