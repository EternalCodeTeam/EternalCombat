package com.eternalcode.combat.region;

import java.util.Collection;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.World;

public interface RegionProvider {

    Optional<Region> getRegion(Location location);

    default boolean isInRegion(Location location) {
        return this.getRegion(location).isPresent();
    }

    Collection<Region> getRegions(World world);

}
