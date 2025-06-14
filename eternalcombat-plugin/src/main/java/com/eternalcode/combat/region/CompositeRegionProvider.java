package com.eternalcode.combat.region;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompositeRegionProvider implements RegionProvider {

    private final List<RegionProvider> providers;

    public CompositeRegionProvider(List<RegionProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        for (RegionProvider provider : this.providers) {
            Optional<Region> region = provider.getRegion(location);
            if (region.isPresent()) {
                return region;
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Region> getRegions(World world) {
        List<Region> combined = new ArrayList<>();
        for (RegionProvider provider : this.providers) {
            combined.addAll(provider.getRegions(world));
        }
        return combined;
    }
}
