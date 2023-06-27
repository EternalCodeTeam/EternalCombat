package com.eternalcode.combat.region;

import org.bukkit.Location;

public class DefaultRegionProvider implements RegionProvider {

    private final int radius;

    private Location firstCorner;
    private Location secondCorner;

    public DefaultRegionProvider(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInRegion(Location location) {
        if (this.firstCorner == null || this.secondCorner == null) {
            this.initializeCorners(location);
        }

        int lowerX = Math.min(this.firstCorner.getBlockX(), this.secondCorner.getBlockX());
        int upperX = Math.max(this.secondCorner.getBlockX(), this.firstCorner.getBlockX());

        int lowerZ = Math.min(this.firstCorner.getBlockZ(), this.secondCorner.getBlockZ());
        int upperZ = Math.max(this.secondCorner.getBlockZ(), this.firstCorner.getBlockZ());

        if (location.getBlockX() > lowerX && location.getBlockX() < upperX) {
            return location.getBlockZ() > lowerZ && location.getBlockZ() < upperZ;
        }

        return false;
    }

    void initializeCorners(Location location) {
        Location center = location.getWorld().getSpawnLocation();

        int lx = center.getBlockX() + this.radius;
        int lz = center.getBlockZ() + this.radius;
        this.firstCorner = new Location(location.getWorld(), lx, 0, lz);

        int px = center.getBlockX() - this.radius;
        int pz = center.getBlockZ() - this.radius;
        this.secondCorner = new Location(location.getWorld(), px, 0, pz);
    }
}
