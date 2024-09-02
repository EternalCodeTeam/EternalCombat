package com.eternalcode.combat.region;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

import java.util.List;

public class WorldGuardRegionProvider implements RegionProvider {

    private final List<String> regions;

    public WorldGuardRegionProvider(List<String> regions) {
        this.regions = regions;
    }

    @Override
    public boolean isInRegion(Location location) {
        return this.regions.stream().anyMatch(region -> this.isInCombatRegion(location, region));
    }

    @Override
    public Location getRegionCenter(Location location) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));

        ProtectedRegion combatRegion = null;

        for (ProtectedRegion region : applicableRegions.getRegions()) {
            if(!isCombatRegion(region)) continue;

            combatRegion = region;
            break;
        }

        if (combatRegion == null) {
            throw new IllegalStateException("Combat region not found.");
        }

        BlockVector3 min = combatRegion.getMinimumPoint();
        BlockVector3 max = combatRegion.getMaximumPoint();

        double x = (double) (min.getX() + max.getX()) / 2;
        double z = (double) (min.getZ() + max.getZ()) / 2;

        return new Location(location.getWorld(), x,location.getY(), z);
    }

    private boolean isInCombatRegion(Location location, String regionName) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));

        return applicableRegions.getRegions().stream().anyMatch(region -> region.getId().equalsIgnoreCase(regionName));
    }

    private boolean isCombatRegion(ProtectedRegion region) {
        return this.regions.stream().anyMatch(regionName -> region.getId().equalsIgnoreCase(regionName));
    }
}
