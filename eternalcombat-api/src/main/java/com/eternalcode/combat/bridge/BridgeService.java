package com.eternalcode.combat.bridge;

import com.eternalcode.combat.bridge.placeholder.FightTagPlaceholder;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.region.DefaultRegionProvider;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.combat.region.WorldGuardRegionProvider;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public class BridgeService {

    private final PluginConfig pluginConfig;
    private final PluginManager pluginManager;
    private final Logger logger;

    private RegionProvider regionProvider;

    public BridgeService(PluginConfig pluginConfig, PluginManager pluginManager, Logger logger) {
        this.pluginConfig = pluginConfig;
        this.pluginManager = pluginManager;
        this.logger = logger;
    }

    public void init(FightManager fightManager, Server server) {
        this.initialize("WorldGuard",
            () -> this.regionProvider = new WorldGuardRegionProvider(this.pluginConfig.settings.blockedRegions),
            () -> {
            this.regionProvider = new DefaultRegionProvider(this.pluginConfig.settings.blockedRegionRadius);

            this.logger.warning("WorldGuard is not installed, set to default region provider.");
        });

        this.initialize("PlaceholderAPI",
            () -> new FightTagPlaceholder(fightManager, server).register(),
            () -> this.logger.warning("PlaceholderAPI is not installed, placeholders will not be registered.")
        );
    }

    private void initialize(String pluginName, BridgeInitializer initializer, Runnable failureHandler) {
        if (this.pluginManager.isPluginEnabled(pluginName)) {
            initializer.initialize();

            this.logger.info("Successfully initialized " + pluginName + " bridge.");
        }
        else {
            failureHandler.run();
        }
    }

    public RegionProvider getRegionProvider() {
        return this.regionProvider;
    }
}
