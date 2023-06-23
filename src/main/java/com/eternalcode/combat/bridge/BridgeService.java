package com.eternalcode.combat.bridge;

import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public class BridgeService {

    private final PluginManager pluginManager;
    private WorldGuardBridge worldGuardBridge;
    private final Logger logger;

    public BridgeService(PluginManager pluginManager, Logger logger) {
        this.pluginManager = pluginManager;
        this.logger = logger;
    }

    public void init() {
        this.initialize("WorldGuard", () -> {
            this.worldGuardBridge = new WorldGuardBridge();
            this.worldGuardBridge.initialize();
        });
    }

    private void initialize(String pluginName, BridgeInitializer initializer) {
        if (this.pluginManager.isPluginEnabled(pluginName)) {
            initializer.initialize();

            this.logger.info("Successfully initialized " + pluginName + " bridge.");
        }
    }

    public WorldGuardBridge worldGuardBridge() {
        return this.worldGuardBridge;
    }
}
