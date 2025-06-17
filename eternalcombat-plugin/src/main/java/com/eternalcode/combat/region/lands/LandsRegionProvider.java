package com.eternalcode.combat.region.lands;

import com.eternalcode.combat.region.ChunkRegion;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;

public class LandsRegionProvider implements RegionProvider {

    private final LandsIntegration lands;

    public LandsRegionProvider(Plugin plugin) {
        this.lands = LandsIntegration.of(plugin);
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        Land land = this.lands.getLandByChunk(location.getWorld(), chunkX, chunkZ);
        if (land == null) {
            return Optional.empty();
        }

        return Optional.of(new ChunkRegion(location.getWorld(), chunkX, chunkZ));
    }

    @Override
    public Collection<Region> getRegions(World world) {
        return this.lands.getLands().stream()
            .map(land -> land.getContainer(world))
            .filter(container -> container != null)
            .flatMap(container -> container.getChunks().stream())
            .map(chunk -> new ChunkRegion(world, chunk.getX(), chunk.getZ()))
            .collect(Collectors.toList());
    }
}
