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
import java.util.Optional;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class BorderServiceImpl implements BorderService {

    private final Scheduler scheduler;
    private final double distance;
    private final int distanceRounded;
    private final double distanceSquared;
    private final Map<String, BorderTriggerIndex> borderIndexes = new HashMap<>();

    public BorderServiceImpl(Scheduler scheduler, Server server, RegionProvider provider, double distance) {
        this.scheduler = scheduler;
        this.distance = distance;
        this.distanceRounded = (int) Math.ceil(distance);
        this.distanceSquared = Math.pow(distance, 2);
        this.scheduler.timerSync(() -> {
            for (World world : server.getWorlds()) {
                this.updateTriggersAsync(world.getName(), provider.getRegions(world));
            }
        }, Duration.ZERO, Duration.ofSeconds(1));
    }

    void updateTriggersAsync(String world, Collection<Region> regions) {
        this.scheduler.async(() -> {
            List<BorderTrigger> triggers = new ArrayList<>();
            for (Region region : regions) {
                Location min = region.getMin();
                Location max = region.getMax();
                triggers.add(new BorderTrigger(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ(), distanceRounded));
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

    private List<BorderPoint> resolveBorderPoints(BorderTrigger trigger, int x, int y, int z) {
        BorderPoint borderMin = trigger.min();
        BorderPoint borderMax = trigger.max();

        int realMinX = Math.max(borderMin.x(), x - distanceRounded);
        int realMaxX = Math.min(borderMax.x(), x + distanceRounded);
        int realMinY = Math.max(borderMin.y(), y - distanceRounded);
        int realMaxY = Math.min(borderMax.y(), y + distanceRounded);
        int realMinZ = Math.max(borderMin.z(), z - distanceRounded);
        int realMaxZ = Math.min(borderMax.z(), z + distanceRounded);

        List<BorderPoint> points = new ArrayList<>();

        if (borderMin.y() >= realMinY) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    addPoint(points, currentX, realMinY, currentZ, x, y, z);
                }
            }
        }

        if (borderMax.y() <= realMaxY) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    addPoint(points, currentX, realMaxY, currentZ, x, y, z);
                }
            }
        }

        if (borderMin.x() >= realMinX) {
            for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    addPoint(points, realMinX, currentY, currentZ, x, y, z);
                }
            }
        }

        if (borderMax.x() <= realMaxX) {
            for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    addPoint(points, realMaxX, currentY, currentZ, x, y, z);
                }
            }
        }

        if (borderMin.z() >= realMinZ) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                    addPoint(points, currentX, currentY, realMinZ, x, y, z);
                }
            }
        }

        if (borderMax.z() <= realMaxZ) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                    addPoint(points, currentX, currentY, realMaxZ, x, y, z);
                }
            }
        }

        return points;
    }

    private void addPoint(List<BorderPoint> points, int x, int y, int z, int playerX, int playerY, int playerZ) {
        if (isNotVisible(x, y, z, playerX, playerY, playerZ)) {
            return;
        }

        points.add(new BorderPoint(x, y, z));
    }

    private boolean isNotVisible(int currentX, int currentY, int currentZ, int x, int y, int z) {
        return Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2) + Math.pow(currentZ - z, 2) > this.distanceSquared;
    }

}
