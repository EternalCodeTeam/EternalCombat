package com.eternalcode.combat.region;

import com.eternalcode.combat.bridge.WorldGuardBridge;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

import java.util.List;

public class RegionService {

    private final WorldGuardBridge worldGuardBridge;

    public RegionService(WorldGuardBridge worldGuardBridge) {
        this.worldGuardBridge = worldGuardBridge;
    }

    private boolean isInCombatRegion(Location location, String regionName) {
        if (this.worldGuardBridge == null || !this.worldGuardBridge.isInitialized()) {
            return false;
        }

        WorldGuard worldGuard = this.worldGuardBridge.worldGuard();
        RegionContainer regionContainer = worldGuard.getPlatform().getRegionContainer();

        RegionQuery regionQuery = regionContainer.createQuery();

        ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));

        return applicableRegions.getRegions().stream().anyMatch(region -> region.getId().equalsIgnoreCase(regionName));
    }

    public boolean isInCombatRegion(Location location, List<String> regions) {
        return regions.stream().anyMatch(region -> this.isInCombatRegion(location, region));
    }
}
