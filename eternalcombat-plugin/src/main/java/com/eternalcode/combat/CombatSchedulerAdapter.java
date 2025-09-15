package com.eternalcode.combat;

import com.eternalcode.commons.bukkit.scheduler.BukkitSchedulerImpl;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import com.eternalcode.commons.folia.scheduler.FoliaSchedulerImpl;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public final class CombatSchedulerAdapter {

    private static final String FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer";

    private CombatSchedulerAdapter() {
        throw new IllegalStateException("You can't instantiate this class");
    }

    public static MinecraftScheduler getAdaptiveScheduler(Plugin plugin) {
        Logger logger = plugin.getLogger();

        try {
            Class.forName(FOLIA_CLASS);
            logger.info("» Detected Folia environment. Using FoliaScheduler.");
            return new FoliaSchedulerImpl(plugin);
        }
        catch (ClassNotFoundException exception) {
            logger.info("» Detected Bukkit/Paper environment. Using BukkitScheduler.");
            return new BukkitSchedulerImpl(plugin);
        }
    }
}
