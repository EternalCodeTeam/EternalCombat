package com.eternalcode.combat.border;

import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.scheduler.Scheduler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

class BorderTriggerIndex {

    private final Server server;
    private final Scheduler scheduler;
    private final RegionProvider provider;
    private final BorderSettings settings;

    private final Map<String, BorderTriggerIndexBucket> borderIndexes = new HashMap<>();

    private BorderTriggerIndex(Server server, Scheduler scheduler, RegionProvider provider, BorderSettings settings) {
        this.server = server;
        this.scheduler = scheduler;
        this.provider = provider;
        this.settings = settings;
    }

    private void updateWorlds() {
        for (World world : server.getWorlds()) {
            this.updateWorld(world.getName(), provider.getRegions(world));
        }
    }

    private void updateWorld(String world, Collection<Region> regions) {
        this.scheduler.async(() -> {
            List<BorderTrigger> triggers = new ArrayList<>();
            for (Region region : regions) {
                Location min = region.getMin();
                Location max = region.getMax();

                triggers.add(new BorderTrigger(
                    min.getBlockX(), min.getBlockY(), min.getBlockZ(),
                    max.getBlockX() + 1, max.getBlockY() + 1, max.getBlockZ() + 1,
                    settings.distanceRounded()
                ));
            }

            BorderTriggerIndexBucket index = BorderTriggerIndexBucket.create(triggers);
            this.borderIndexes.put(world, index);
        });
    }

    static BorderTriggerIndex started(Server server, Scheduler scheduler, RegionProvider provider, BorderSettings settings) {
        BorderTriggerIndex index = new BorderTriggerIndex(server, scheduler, provider, settings);
        scheduler.timerSync(() -> index.updateWorlds(), Duration.ZERO, settings.indexRefreshDelay());
        return index;
    }

    public List<BorderTrigger> getTriggered(Location location) {
        BorderTriggerIndexBucket index = borderIndexes.get(location.getWorld().getName());
        if (index == null) {
            return List.of();
        }

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());

        return index.getTriggers(x, z)
            .stream()
            .filter(trigger -> trigger.isTriggered(x, y, z))
            .toList();
    }

}
