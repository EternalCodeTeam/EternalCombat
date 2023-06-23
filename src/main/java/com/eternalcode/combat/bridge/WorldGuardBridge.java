package com.eternalcode.combat.bridge;

import com.sk89q.worldguard.WorldGuard;

public class WorldGuardBridge {

    private boolean initialized;
    private WorldGuard worldGuard;

    public void initialize() {
        if (this.initialized) {
            return;
        }

        this.initialized = true;
        this.worldGuard = WorldGuard.getInstance();
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public WorldGuard worldGuard() {
        return this.worldGuard;
    }
}
