package com.eternalcode.combat.region;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeSet;
import org.bukkit.Location;

import java.util.List;
import org.bukkit.World;

public class WorldGuardRegionProvider implements RegionProvider {

    private final RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
    private final TreeSet<String> regions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final PluginConfig pluginConfig;

    public WorldGuardRegionProvider(List<String> regions, PluginConfig pluginConfig) {
        this.regions.addAll(regions);
        this.pluginConfig = pluginConfig;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        RegionQuery regionQuery = this.regionContainer.createQuery();
        ApplicableRegionSet applicableRegions = regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));

        for (ProtectedRegion region : applicableRegions.getRegions()) {
            if (!this.isCombatRegion(region)) {
                continue;
            }

            return Optional.of(new WorldGuardRegion(location.getWorld(), region));
        }

        return Optional.empty();
    }

    @Override
    public Collection<Region> getRegions(World world) {
        RegionManager regionManager = this.regionContainer.get(BukkitAdapter.adapt(world));
        if (regionManager == null) {
            return Collections.emptyList();
        }

        return regionManager.getRegions()
            .values()
            .stream()
            .filter(region -> this.isCombatRegion(region))
            .map(region -> (Region) new WorldGuardRegion(world, region))
            .toList();
    }

    private boolean isCombatRegion(ProtectedRegion region) {
        if (this.regions.contains(region.getId())) {
            return true;
        }

        if (this.pluginConfig.settings.preventPvpInRegions) {
            StateFlag.State flag = region.getFlag(Flags.PVP);

            if (flag != null) {
                return flag.equals(StateFlag.State.DENY);
            }
        }

        return false;
    }

    private record WorldGuardRegion(World context, ProtectedRegion region) implements Region {
        @Override
        public Location getCenter() {
            BlockVector3 min = this.region.getMinimumPoint();
            BlockVector3 max = this.region.getMaximumPoint();

            double x = (double) (min.getX() + max.getX()) / 2;
            double z = (double) (min.getZ() + max.getZ()) / 2;
            double y = (double) (min.getY() + max.getY()) / 2;

            return new Location(this.context, x, y, z);
        }

        @Override
        public Location getMin() {
            BlockVector3 min = this.region.getMinimumPoint();
            return new Location(this.context, min.getX(), min.getY(), min.getZ());
        }

        @Override
        public Location getMax() {
            BlockVector3 max = this.region.getMaximumPoint();
            return new Location(this.context, max.getX(), max.getY(), max.getZ());
        }
    }

}
