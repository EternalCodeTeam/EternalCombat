package com.eternalcode.combat.region.worldguard;

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

        double x = (min.x() + max.x()) / 2.0;
        double z = (min.z() + max.z()) / 2.0;

        return new Point(this.world, x, z);
    }

    @Override
    public Location getMin() {
        BlockVector3 min = this.region.getMinimumPoint();
        return new Location(this.world, min.x(), min.y(), min.z());
    }

    @Override
    public Location getMax() {
        BlockVector3 max = this.region.getMaximumPoint();
        return new Location(this.world, max.x(), max.y(), max.z());
    }

}
