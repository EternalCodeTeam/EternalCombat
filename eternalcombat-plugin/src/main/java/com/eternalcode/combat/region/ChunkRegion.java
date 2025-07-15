package com.eternalcode.combat.region;

import org.bukkit.Location;
import org.bukkit.World;

public record ChunkRegion(World world, int chunkX, int chunkZ) implements Region {

    @Override
    public Point getCenter() {
        int centerX = (chunkX << 4) + 8;
        int centerZ = (chunkZ << 4) + 8;
        return new Point(world, centerX + 0.5, centerZ + 0.5);
    }

    @Override
    public Location getMin() {
        int minX = chunkX << 4;
        int minZ = chunkZ << 4;
        return new Location(world, minX, world.getMinHeight(), minZ);
    }

    @Override
    public Location getMax() {
        int maxX = (chunkX << 4) + 15;
        int maxZ = (chunkZ << 4) + 15;
        return new Location(world, maxX, world.getMaxHeight() - 1, maxZ);
    }

}