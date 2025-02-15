package com.eternalcode.combat.region;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class DefaultRegionProvider implements RegionProvider {

    private final int radius;

    public DefaultRegionProvider(int radius) {
        this.radius = radius;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        World world = location.getWorld();
        Region spawnRegion = createSpawnRegion(world);
        if (spawnRegion.contains(location.getX(), location.getY(), location.getZ())) {
            return Optional.of(spawnRegion);
        }

        return Optional.empty();
    }

    @Override
    public Collection<Region> getRegions(World world) {
        Region spawnRegion = createSpawnRegion(world);
        return List.of(spawnRegion);
    }

    private Region createSpawnRegion(World world) {
        Location spawnLocation = world.getSpawnLocation();
        double x = spawnLocation.getX();
        double z = spawnLocation.getZ();

        Location min = new Location(world, x - this.radius, world.getMinHeight(), z - this.radius);
        Location max = new Location(world, x + this.radius, world.getMaxHeight(), z + this.radius);

        return new DefaultSpawnRegion(min, max, spawnLocation);
    }

    private record DefaultSpawnRegion(Location min, Location max, Location center) implements Region {
        @Override
        public Location getCenter() {
            return this.center;
        }

        @Override
        public Location getMin() {
            return this.min;
        }

        @Override
        public Location getMax() {
            return this.max;
        }
    }

}
