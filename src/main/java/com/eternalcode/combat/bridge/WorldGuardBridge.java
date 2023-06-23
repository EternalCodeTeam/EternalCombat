package com.eternalcode.combat.bridge;

import com.sk89q.worldguard.WorldGuard;

public class WorldGuardBridge {

    private boolean initialized;

    public void initialize() {
        if (this.initialized) {
            return;
        }

        this.initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public WorldGuard worldGuard() {
        return WorldGuard.getInstance();
    }
}
