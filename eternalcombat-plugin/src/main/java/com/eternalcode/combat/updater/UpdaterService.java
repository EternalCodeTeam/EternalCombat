package com.eternalcode.combat.updater;

import com.eternalcode.commons.updater.UpdateResult;
import com.eternalcode.commons.updater.Version;
import com.eternalcode.commons.updater.impl.ModrinthUpdateChecker;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.plugin.PluginDescriptionFile;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class UpdaterService {

    private static final String MODRINTH_PROJECT_ID = "EternalCombat";
    private static final String CACHE_KEY = "modrinth-update";

    private final AsyncLoadingCache<String, UpdateResult> updateCache;

    public UpdaterService(PluginDescriptionFile descriptionFile) {
        Version currentVersion = new Version(descriptionFile.getVersion());
        ModrinthUpdateChecker updateChecker = new ModrinthUpdateChecker();

        this.updateCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .buildAsync(key -> updateChecker.check(MODRINTH_PROJECT_ID, currentVersion));
    }

    CompletableFuture<UpdateResult> checkForUpdate() {
        return this.updateCache.get(CACHE_KEY);
    }

}
