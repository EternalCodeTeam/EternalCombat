package com.eternalcode.combat;

import com.eternalcode.commons.bukkit.scheduler.BukkitSchedulerImpl;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import com.eternalcode.commons.folia.scheduler.FoliaSchedulerImpl;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public final class CombatSchedulerAdapter {

    private CombatSchedulerAdapter() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    public static MinecraftScheduler getAdaptiveScheduler(Plugin plugin) {
        Logger logger = plugin.getLogger();

        if (FoliaChecker.isFolia()) {
            logger.info("» Detected Folia environment. Using FoliaScheduler.");
            return new FoliaSchedulerImpl(plugin);
        }

        logger.info("» Detected Bukkit/Paper environment. Using BukkitScheduler.");
        return new BukkitSchedulerImpl(plugin);
    }
}
