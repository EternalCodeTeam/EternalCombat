package com.eternalcode.combat.border;

import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.scheduler.Scheduler;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class BorderServiceImpl implements BorderService {

    private final Scheduler scheduler;
    private final int distance;
    private final Map<String, BorderTriggerIndex> borderIndexes = new HashMap<>();

    public BorderServiceImpl(Scheduler scheduler, Server server, RegionProvider provider, int distance) {
        this.scheduler = scheduler;
        this.distance = distance;
        this.scheduler.timerSync(() -> {
            for (World world : server.getWorlds()) {
                this.updateTriggersAsync(world.getName(), provider.getRegions(world.getName()));
            }
        }, Duration.ZERO, Duration.ofSeconds(1));
    }

    void updateTriggersAsync(String world, Collection<Region> regions) {
        this.scheduler.async(() -> {
            List<BorderTrigger> triggers = new ArrayList<>();
            for (Region region : regions) {
                Location min = region.getMin();
                Location max = region.getMax();
                triggers.add(new BorderTrigger(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ(), distance));
            }

            this.borderIndexes.put(world, BorderTriggerIndex.create(triggers));
        });
    }

    @Override
    public Optional<BorderResult> resolveBorder(Location location) {
        BorderTriggerIndex index = borderIndexes.get(location.getWorld().getName());
        if (index == null) {
            return Optional.empty();
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        Set<BorderTrigger> triggers = index.getTriggers(x, z);
        LazyBorderResult result = new LazyBorderResult();

        for (BorderTrigger trigger : triggers) {
            if (!trigger.isTriggered(x, y, z)) {
                continue;
            }

            result.addLazyBorderPoints(() -> this.resolveBorderPoints(trigger, x, y, z));
        }

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    private Set<BorderPoint> resolveBorderPoints(BorderTrigger trigger, int x, int y, int z) {
        BorderPoint borderMin = trigger.min();
        BorderPoint borderMax = trigger.max();

        int realMaxX = Math.min(borderMax.x(), x + distance);
        int realMaxY = Math.min(borderMax.y(), y + distance);
        int realMaxZ = Math.min(borderMax.z(), z + distance);

        Set<BorderPoint> points = new HashSet<>();
        for (int currentX = Math.max(borderMin.x(), x - distance); currentX <= realMaxX; currentX++) {
            for (int currentY = Math.max(borderMin.y(), y - distance); currentY <= realMaxY; currentY++) {
                for (int currentZ = Math.max(borderMin.z(), z - distance); currentZ <= realMaxZ; currentZ++) {
                    if (isBorder(borderMin, borderMax, currentX, currentY, currentZ)) {
                        points.add(new BorderPoint(currentX, currentY, currentZ));
                    }
                }
            }
        }

        return points;
    }

    private static boolean isBorder(BorderPoint borderMin, BorderPoint borderMax, int currentX, int currentY, int currentZ) {
        return currentX == borderMin.x() || currentX == borderMax.x() ||
            currentY == borderMin.y() || currentY == borderMax.y() ||
            currentZ == borderMin.z() || currentZ == borderMax.z();
    }

}
