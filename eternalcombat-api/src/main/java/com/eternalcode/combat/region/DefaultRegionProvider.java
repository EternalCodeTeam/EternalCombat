package com.eternalcode.combat.region;

import java.util.Optional;
import org.bukkit.Location;

public class DefaultRegionProvider implements RegionProvider {

    private final int radius;

    public DefaultRegionProvider(int radius) {
        this.radius = radius;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        Location spawnLocation = location.getWorld().getSpawnLocation();
        double x = spawnLocation.getX();
        double z = spawnLocation.getZ();

        Point min = new Point(x - this.radius, z - this.radius);
        Point max = new Point(x + this.radius, z + this.radius);

        if (this.contains(min, max, location.getX(), location.getZ())) {
            return Optional.of(() -> new Location(location.getWorld(), x, location.getY(), z));
        }

        return Optional.empty();
    }

    private boolean contains(Point min, Point max, double x, double z) {
        return x >= min.x() && x < max.x()
            && z >= min.z() && z < max.z();
    }

    private record Point(double x, double z) {}

}
