package com.eternalcode.combat.region.access;

import com.eternalcode.combat.region.Region;
import org.bukkit.entity.Player;

public class DefaultRegionAccessController implements RegionAccessController {

    private final RegionAccessConfig config;

    public DefaultRegionAccessController(RegionAccessConfig config) {
        this.config = config;
    }

    @Override
    public boolean canEnter(Player player, Region region) {
        if (player.hasPermission(config.bypassPermission)) {
            return true;
        }
        return config.defaultAllow;
    }
}

