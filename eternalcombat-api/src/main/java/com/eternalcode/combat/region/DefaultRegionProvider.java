package com.eternalcode.combat.region;

import org.bukkit.Location;

public class DefaultRegionProvider implements RegionProvider {

    private final int radius;

    public DefaultRegionProvider(int radius) {
        this.radius = radius;
    }

    @Override
    public boolean isInRegion(Location location) {
        Location spawnLocation = location.getWorld().getSpawnLocation();
        double x = spawnLocation.getX();
        double z = spawnLocation.getZ();

        Point min = new Point(x - this.radius, z - this.radius);
        Point max = new Point(x + this.radius, z + this.radius);

        return this.contains(min, max, location.getX(), location.getZ());
    }

    @Override
    public Location getRegionCenter(Location location) {
        Location spawnLocation = location.getWorld().getSpawnLocation();
        double x = spawnLocation.getX();
        double z = spawnLocation.getZ();

        return new Location(location.getWorld(), x, location.getY(), z);
    }

    public boolean contains(Point min, Point max, double x, double z) {
        return x >= min.x() && x < max.x()
            && z >= min.z() && z < max.z();
    }

    public record Point(double x, double z) {}

}
