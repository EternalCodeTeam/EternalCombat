package com.eternalcode.combat.region;

import org.bukkit.Location;

@FunctionalInterface
public interface RegionProvider {

    boolean isInRegion(Location location);

}
