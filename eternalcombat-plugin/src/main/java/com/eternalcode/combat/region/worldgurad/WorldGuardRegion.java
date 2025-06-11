package com.eternalcode.combat.region.worldgurad;

import com.eternalcode.combat.region.Point;
import com.eternalcode.combat.region.Region;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;

record WorldGuardRegion(World world, ProtectedRegion region) implements Region {
    @Override
    public Point getCenter() {
        BlockVector3 min = this.region.getMinimumPoint();
        BlockVector3 max = this.region.getMaximumPoint();

        double x = (double) (min.getX() + max.getX()) / 2;
        double z = (double) (min.getZ() + max.getZ()) / 2;

        return new Point(this.world, x, z);
    }

    @Override
    public Location getMin() {
        BlockVector3 min = this.region.getMinimumPoint();
        return new Location(this.world, min.getX(), min.getY(), min.getZ());
    }

    @Override
    public Location getMax() {
        BlockVector3 max = this.region.getMaximumPoint();
        return new Location(this.world, max.getX(), max.getY(), max.getZ());
    }
}
