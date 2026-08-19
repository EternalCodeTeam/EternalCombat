package com.eternalcode.combat.region.griefprevention;

import com.eternalcode.combat.region.Point;
import com.eternalcode.combat.region.Region;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Location;
import org.bukkit.World;

record GriefPreventionRegion(World world, Claim claim) implements Region {

    @Override
    public Point getCenter() {
        Location min = this.claim.getLesserBoundaryCorner();
        Location max = this.claim.getGreaterBoundaryCorner();

        double x = (min.getX() + max.getX()) / 2.0;
        double z = (min.getZ() + max.getZ()) / 2.0;

        return new Point(this.world, x, z);
    }

    @Override
    public Location getMin() {
        Location min = this.claim.getLesserBoundaryCorner();
        return new Location(this.world, min.getBlockX(), this.world.getMinHeight(), min.getBlockZ());
    }

    @Override
    public Location getMax() {
        Location max = this.claim.getGreaterBoundaryCorner();
        return new Location(this.world, max.getBlockX(), this.world.getMaxHeight() - 1, max.getBlockZ());
    }

}
