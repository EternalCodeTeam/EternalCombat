package com.eternalcode.combat.region;

import org.bukkit.Location;
import org.bukkit.World;

public record Point(World world, double x, double z) {

    public Location toLocation() {
        return new Location(world, x, 64, z);
    }

}
