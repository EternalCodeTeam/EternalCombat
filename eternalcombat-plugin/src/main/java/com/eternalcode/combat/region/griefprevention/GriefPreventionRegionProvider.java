package com.eternalcode.combat.region.griefprevention;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.World;

public class GriefPreventionRegionProvider implements RegionProvider {

    private final TreeSet<String> claims = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    private final PluginConfig pluginConfig;

    public GriefPreventionRegionProvider(PluginConfig pluginConfig) {
        this.claims.addAll(pluginConfig.regions.blockedRegions);
        this.pluginConfig = pluginConfig;
    }

    @Override
    public Optional<Region> getRegion(Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);

        if (claim == null || !this.isCombatClaim(claim)) {
            return Optional.empty();
        }

        return Optional.of(new GriefPreventionRegion(location.getWorld(), claim));
    }

    @Override
    public Collection<Region> getRegions(World world) {
        List<Region> regions = new ArrayList<>();

        for (Claim claim : this.snapshotClaims()) {
            if (!world.equals(claim.getLesserBoundaryCorner().getWorld())) {
                continue;
            }

            if (this.isCombatClaim(claim)) {
                regions.add(new GriefPreventionRegion(world, claim));
            }
        }

        return regions;
    }

    private boolean isCombatClaim(Claim claim) {
        if (this.claims.contains(String.valueOf(claim.getID()))) {
            return true;
        }

        if (!this.pluginConfig.regions.preventPvpInRegions) {
            return false;
        }

        World world = claim.getLesserBoundaryCorner().getWorld();
        GriefPrevention griefPrevention = GriefPrevention.instance;

        if (world == null || !griefPrevention.pvpRulesApply(world)) {
            return false;
        }

        if (!claim.isAdminClaim()) {
            return griefPrevention.config_pvp_noCombatInPlayerLandClaims;
        }

        if (claim.parent == null) {
            return griefPrevention.config_pvp_noCombatInAdminLandClaims;
        }

        return griefPrevention.config_pvp_noCombatInAdminSubdivisions;
    }

    // GriefPrevention returns a live view of its claim list, and the border index reads regions asynchronously.
    // A failed snapshot is retried by the next border index refresh, so an empty result is safe here.
    private List<Claim> snapshotClaims() {
        try {
            return List.copyOf(GriefPrevention.instance.dataStore.getClaims());
        }
        catch (ConcurrentModificationException exception) {
            return List.of();
        }
    }

}
