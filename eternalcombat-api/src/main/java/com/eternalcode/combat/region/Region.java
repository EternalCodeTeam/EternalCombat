package com.eternalcode.combat.region;

import org.bukkit.Location;
import org.bukkit.World;

public interface Region {

    Point getCenter();

    Location getMin();

    Location getMax();

    default boolean contains(Location location) {
        if (location == null) {
            return false;
        }

        Location min = this.getMin();
        World regionWorld = min.getWorld();
        World locationWorld = location.getWorld();

        if (regionWorld == null || locationWorld == null || !regionWorld.equals(locationWorld)) {
            return false;
        }

        return this.contains(location.getX(), location.getY(), location.getZ());
    }

    default boolean contains(double x, double y, double z) {
        Location min = this.getMin();
        Location max = this.getMax();
        return x >= min.getX() && x < max.getX()
            && y >= min.getY() && y < max.getY()
            && z >= min.getZ() && z < max.getZ();
    }

}
