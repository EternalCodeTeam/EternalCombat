package com.eternalcode.combat.bridge.lands;

import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import me.angeschossen.lands.api.land.Container;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class LandsRegionProvider implements RegionProvider {

    private final LandsIntegration lands;

    public LandsRegionProvider(LandsIntegration lands) {
        this.lands = lands;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        Area area = this.lands.getArea(location);
        if (area == null) {
            return Optional.empty();
        }

        Land land = area.getLand();
        World world = location.getWorld();

        Container container = land.getContainer(world);
        if (container == null) {
            return Optional.empty();
        }

        Collection<? extends ChunkCoordinate> chunks = container.getChunks();
        if (chunks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new LandChunksRegion(world, chunks));
    }

    @Override
    public Collection<Region> getRegions(World world) {
        return this.lands.getLands().stream()
            .map(land -> land.getContainer(world))
            .filter(container -> container != null && !container.getChunks().isEmpty())
            .map(container -> new LandChunksRegion(world, container.getChunks()))
            .collect(Collectors.toList());
    }
}
