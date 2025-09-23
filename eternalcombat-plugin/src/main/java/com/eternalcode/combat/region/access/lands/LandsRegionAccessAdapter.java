package com.eternalcode.combat.region.access.lands;

import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.access.RegionAccessConfig;
import com.eternalcode.combat.region.access.RegionAccessController;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.nation.Nation;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LandsRegionAccessAdapter implements RegionAccessController {

    private final LandsIntegration lands;
    private final RegionAccessConfig config;

    public LandsRegionAccessAdapter(Plugin plugin, RegionAccessConfig config) {
        this.lands = LandsIntegration.of(plugin);
        this.config = config;
    }

    @Override
    public boolean canEnter(Player player, Region region) {
        if (!config.wartimeEnabled) {
            return false;
        }

        LandPlayer landPlayer = lands.getLandPlayer(player.getUniqueId());
        if (landPlayer == null) {
            return false;
        }

        Location regionLocation = getRegionLocation(region);
        if (regionLocation == null) {
            return false;
        }

        Land targetLand = lands.getLandByChunk(
            regionLocation.getWorld(),
            regionLocation.getBlockX() >> 4,
            regionLocation.getBlockZ() >> 4
        );

        if (targetLand == null) {
            return false;
        }

        boolean isOwner = landPlayer.getLands().contains(targetLand);
        if (isOwner) {
            return true;
        }

        boolean atWar = areNationsAtWar(landPlayer, targetLand);
        boolean result = atWar;

        return result;
    }

    private boolean areNationsAtWar(LandPlayer landPlayer, Land targetLand) {
        try {
            Nation playerNation = getPlayerNation(landPlayer);
            Nation targetNation = targetLand.getNation();

            return playerNation != null && targetNation != null &&
                (playerNation.isEnemy(targetNation) || targetNation.isEnemy(playerNation));
        }
        catch (Exception e) {
            return false;
        }
    }

    private Nation getPlayerNation(LandPlayer landPlayer) {
        return landPlayer.getLands().stream()
            .filter(land -> land != null)
            .map(Land::getNation)
            .filter(nation -> nation != null)
            .findFirst()
            .orElse(null);
    }

    private Location getRegionLocation(Region region) {
        try {
            return region.getCenter().toLocation();
        }
        catch (Exception exception) {
            return null;
        }
    }
}
