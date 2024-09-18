package com.eternalcode.combat.region;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Location;

import java.util.List;

public class WorldGuardRegionProvider implements RegionProvider {

    private final List<String> regions;
    private final PluginConfig pluginConfig;


    public WorldGuardRegionProvider(List<String> regions, PluginConfig pluginConfig) {
        this.regions = regions;
        this.pluginConfig = pluginConfig;
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

        AtomicReference<ProtectedRegion> combatRegion = new AtomicReference<>();

        for (ProtectedRegion region : applicableRegions.getRegions()) {
            if (!isCombatRegion(region)) continue;

            combatRegion.set(region);
            break;
        }

        if (combatRegion.get() == null) {
            throw new IllegalStateException("Combat region not found.");
        }

        BlockVector3 min = combatRegion.get().getMinimumPoint();
        BlockVector3 max = combatRegion.get().getMaximumPoint();

        double x = (double) (min.getX() + max.getX()) / 2;
        double z = (double) (min.getZ() + max.getZ()) / 2;

        return new Location(location.getWorld(), x, location.getY(), z);
    }

    private boolean isInCombatRegion(Location location, String regionName) {
        RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery regionQuery = regionContainer.createQuery();
        ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));

        if (applicableRegions.getRegions().stream().anyMatch(region -> region.getId().equalsIgnoreCase(regionName))) {
            return true;
        }

        if (this.pluginConfig.settings.shouldPreventPvpRegions) {
            for (ProtectedRegion region : applicableRegions.getRegions()) {

                return region.getFlag(Flags.PVP) != null && region.getFlag(Flags.PVP).equals(StateFlag.State.DENY);
            }
        }

        return false;
    }

    private boolean isCombatRegion(ProtectedRegion region) {
        if (this.regions.stream().anyMatch(regionName -> region.getId().equalsIgnoreCase(regionName))) {
            return true;
        }

        if (this.pluginConfig.settings.shouldPreventPvpRegions && region.getFlag(Flags.PVP) != null) {
            return region.getFlag(Flags.PVP).equals(StateFlag.State.DENY);
        }


        return false;
    }
}
