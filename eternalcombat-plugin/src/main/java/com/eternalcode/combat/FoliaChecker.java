package com.eternalcode.combat;

public final class FoliaChecker {

    private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";
    private static Boolean isFoliaPresent = null;

    private FoliaChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isFolia() {
        return detectFoliaEnvironment();
    }

    private static synchronized boolean detectFoliaEnvironment() {
        if (isFoliaPresent != null) {
            return isFoliaPresent;
        }

        try {
            Class.forName(FOLIA_CLASS);
            isFoliaPresent = true;
        }
        catch (ClassNotFoundException exception) {
            isFoliaPresent = false;
        }

        return isFoliaPresent;
    }

    public static void clearCache() {
        isFoliaPresent = null;
    }

}
