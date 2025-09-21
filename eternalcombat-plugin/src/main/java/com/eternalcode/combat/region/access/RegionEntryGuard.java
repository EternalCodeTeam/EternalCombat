package com.eternalcode.combat.region.access;

import com.eternalcode.combat.region.Region;
import java.util.List;
import org.bukkit.entity.Player;

public class RegionEntryGuard {

    private final List<RegionAccessController> controllers;
    private final RegionAccessConfig config;

    public RegionEntryGuard(List<RegionAccessController> controllers, RegionAccessConfig config) {
        this.controllers = controllers;
        this.config = config;
    }

    public boolean canEnter(Player player, Region region) {
        // Sprawdź uprawnienia bypass na poziomie głównym
        if (player.hasPermission(config.bypassPermission)) {
            return true;
        }

        if (controllers.isEmpty()) {
            return config.defaultAllow;
        }

        return controllers.stream()
            .anyMatch(controller -> {
                try {
                    return controller.canEnter(player, region);
                }
                catch (Exception exception) {
                    return false;
                }
            });
    }
}

