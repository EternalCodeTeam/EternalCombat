package com.eternalcode.combat;

import com.eternalcode.commons.bukkit.scheduler.BukkitSchedulerImpl;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import com.eternalcode.commons.folia.scheduler.FoliaSchedulerImpl;
import org.bukkit.plugin.Plugin;

public final class CombatSchedulerAdapter {

    private CombatSchedulerAdapter() {
        throw new UnsupportedOperationException("This is an utility class and cannot be instantiated");
    }

    public static MinecraftScheduler getAdaptiveScheduler(Plugin plugin) {
        if (FoliaChecker.isFolia(plugin)) {
            return new FoliaSchedulerImpl(plugin);
        }

        return new BukkitSchedulerImpl(plugin);
    }
}
