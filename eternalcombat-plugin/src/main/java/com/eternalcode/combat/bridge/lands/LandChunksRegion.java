package com.eternalcode.combat.bridge.lands;

import com.eternalcode.combat.region.Region;
import java.util.Collection;
import me.angeschossen.lands.api.land.ChunkCoordinate;
import org.bukkit.Location;
import org.bukkit.World;

class LandChunksRegion implements Region {

    private final World world;
    private final int minX, minZ, maxX, maxZ;

    public LandChunksRegion(World world, Collection<? extends ChunkCoordinate> chunks) {
        this.world = world;

        int _minX = Integer.MAX_VALUE, _minZ = Integer.MAX_VALUE;
        int _maxX = Integer.MIN_VALUE, _maxZ = Integer.MIN_VALUE;

        for (ChunkCoordinate chunkCoordinate : chunks) {
            int bx = chunkCoordinate.getBlockX();
            int bz = chunkCoordinate.getBlockZ();
            _minX = Math.min(_minX, bx);
            _minZ = Math.min(_minZ, bz);
            _maxX = Math.max(_maxX, bx);
            _maxZ = Math.max(_maxZ, bz);
        }

        this.minX = _minX;
        this.minZ = _minZ;
        this.maxX = _maxX + 15;
        this.maxZ = _maxZ + 15;
    }

    @Override
    public Location getCenter() {
        double cx = (this.minX + this.maxX) / 2.0;
        double cz = (this.minZ + this.maxZ) / 2.0;
        return new Location(this.world, cx, 64, cz);
    }

    @Override
    public Location getMin() {
        return new Location(this.world, this.minX, 0, this.minZ);
    }

    @Override
    public Location getMax() {
        return new Location(this.world, this.maxX, this.world.getMaxHeight() - 1, this.maxZ);
    }
}
