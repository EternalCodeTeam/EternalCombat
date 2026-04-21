package com.eternalcode.combat;

public final class FoliaChecker {

    private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";

    private FoliaChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isFolia() {
        return detectFoliaEnvironment();
    }

    private static boolean detectFoliaEnvironment() {
        try {
            Class.forName(FOLIA_CLASS);
            return true;
        }
        catch (ClassNotFoundException exception) {
            return false;
        }
    }

}
