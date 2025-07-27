package com.eternalcode.combat.fight.knockback;

import org.bukkit.Location;
import org.bukkit.World;

record Point2D(int x, int z) {

    Location toLocation(Location location) {
        World world = location.getWorld();
        int y = world.getHighestBlockYAt(x, z) + 1;

        return new Location(world, x, y, z, location.getYaw(), location.getPitch());
    }

    static Point2D from(Location location) {
        return new Point2D(location.getBlockX(), location.getBlockZ());
    }

    public Point2D min(Location min) {
        return new Point2D(Math.min(this.x, min.getBlockX()), Math.min(this.z, min.getBlockZ()));
    }

    public Point2D max(Location max) {
        return new Point2D(Math.max(this.x, max.getBlockX()), Math.max(this.z, max.getBlockZ()));
    }

}
