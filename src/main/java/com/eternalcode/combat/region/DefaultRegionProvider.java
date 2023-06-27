package com.eternalcode.combat.region;

import org.bukkit.Location;

public class DefaultRegionProvider implements RegionProvider {

    private final int radius;

    public DefaultRegionProvider(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInRegion(Location location) {
        return location.getWorld().getSpawnLocation().distance(location) <= this.radius;
    }
}
