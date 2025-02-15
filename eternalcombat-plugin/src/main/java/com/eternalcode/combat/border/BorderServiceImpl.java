package com.eternalcode.combat.border;

import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.combat.event.EventCaller;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.scheduler.Scheduler;
import dev.rollczi.litecommands.shared.Lazy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BorderServiceImpl implements BorderService {

    private final Scheduler scheduler;
    private final EventCaller eventCaller;

    private final int distanceRounded;
    private final double distance;

    private final Map<String, BorderTriggerIndex> borderIndexes = new HashMap<>();
    private final BorderActivePointsRegistry activeBorderPoints = new BorderActivePointsRegistry();

    public BorderServiceImpl(Scheduler scheduler, Server server, RegionProvider provider, EventCaller eventCaller, double distance) {
        this.scheduler = scheduler;
        this.eventCaller = eventCaller;
        this.distanceRounded = (int) Math.ceil(distance);
        this.distance = distance;
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

            BorderTriggerIndex index = BorderTriggerIndex.create(triggers);
            this.borderIndexes.put(world, index);
        });
    }

    @Override
    public void updateBorder(Player player, Location location) {
        Optional<BorderResult> result = resolveBorder(location);
        String world = player.getWorld().getName();

        if (result.isEmpty()) {
            if (this.activeBorderPoints.hasPoints(world, player.getUniqueId())) {
                return;
            }

            scheduler.async(() -> {
                Set<BorderPoint> borderPoints = this.activeBorderPoints.removePoints(world, player.getUniqueId());
                if (!borderPoints.isEmpty()) {
                    eventCaller.publishEvent(new BorderHideAsyncEvent(player, borderPoints));
                }
            });
            return;
        }

        scheduler.async(() -> {
            BorderResult borderResult = result.get();
            Set<BorderPoint> points = borderResult.stream()
                .collect(Collectors.toSet());

            if (!points.isEmpty()) {
                BorderShowAsyncEvent event = eventCaller.publishEvent(new BorderShowAsyncEvent(player, points));
                points = event.getPoints();
            }

            Set<BorderPoint> toRemove = this.activeBorderPoints.putPoints(world, player.getUniqueId(), points); // async

            if (!toRemove.isEmpty()) { // async
                eventCaller.publishEvent(new BorderHideAsyncEvent(player, toRemove)); // async
            }
        });
    }

    @Override
    public Set<BorderPoint> getActiveBorder(Player player) {
        return this.activeBorderPoints.getPoints(player.getWorld().getName(), player.getUniqueId());
    }

    private Optional<BorderResult> resolveBorder(Location location) {
        BorderTriggerIndex index = borderIndexes.get(location.getWorld().getName());
        if (index == null) {
            return Optional.empty();
        }

        int x = (int) Math.round(location.getX());
        int y = (int) Math.round(location.getY());
        int z = (int) Math.round(location.getZ());
        Set<BorderTrigger> triggers = index.getTriggers(x, z);
        LazyBorderResult result = new LazyBorderResult();

        for (BorderTrigger trigger : triggers) {
            if (!trigger.isTriggered(x, y, z)) {
                continue;
            }

            result.addLazyBorderPoints(new Lazy<>(() -> this.resolveBorderPoints(trigger, location)));
        }

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    private List<BorderPoint> resolveBorderPoints(BorderTrigger trigger, Location playerLocation) {
        BorderPoint borderMin = trigger.min();
        BorderPoint borderMax = trigger.max();

        int x = (int) Math.round(playerLocation.getX());
        int y = (int) Math.round(playerLocation.getY());
        int z = (int) Math.round(playerLocation.getZ());

        int realMinX = Math.max(borderMin.x(), x - distanceRounded);
        int realMaxX = Math.min(borderMax.x(), x + distanceRounded);
        int realMinY = Math.max(borderMin.y(), y - distanceRounded);
        int realMaxY = Math.min(borderMax.y(), y + distanceRounded);
        int realMinZ = Math.max(borderMin.z(), z - distanceRounded);
        int realMaxZ = Math.min(borderMax.z(), z + distanceRounded);

        List<BorderPoint> points = new ArrayList<>();

        if (borderMin.y() >= realMinY) {
            for (int currentX = realMinX; currentX <= realMaxX - 1; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ - 1; currentZ++) {
                    addPoint(points, currentX, realMinY, currentZ, playerLocation, null);
                }
            }
        }

        if (borderMax.y() <= realMaxY) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    BorderPoint inclusive = new BorderPoint(Math.max(realMinX, currentX - 1), realMaxY - 1, Math.max(realMinZ, currentZ - 1));
                    addPoint(points, currentX, realMaxY, currentZ, playerLocation, inclusive);
                }
            }
        }

        if (borderMin.x() >= realMinX) {
            for (int currentY = realMinY; currentY <= realMaxY - 1; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ - 1; currentZ++) {
                    addPoint(points, realMinX, currentY, currentZ, playerLocation, null);
                }
            }
        }

        if (borderMax.x() <= realMaxX) {
            for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    BorderPoint inclusive = new BorderPoint(realMaxX - 1, Math.max(realMinY, currentY - 1), Math.max(realMinZ, currentZ - 1));
                    addPoint(points, realMaxX, currentY, currentZ, playerLocation, inclusive);
                }
            }
        }

        if (borderMin.z() >= realMinZ) {
            for (int currentX = realMinX; currentX <= realMaxX - 1; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY - 1; currentY++) {
                    addPoint(points, currentX, currentY, realMinZ, playerLocation, null);
                }
            }
        }

        if (borderMax.z() <= realMaxZ) {
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                    BorderPoint inclusive = new BorderPoint(Math.max(realMinX, currentX - 1), Math.max(realMinY, currentY - 1), realMaxZ - 1);
                    addPoint(points, currentX, currentY, realMaxZ, playerLocation, inclusive);
                }
            }
        }

        return points;
    }

    private void addPoint(List<BorderPoint> points, int x, int y, int z, Location playerLocation, BorderPoint inclusive) {
        if (isNotVisible(x, y, z, playerLocation)) {
            return;
        }

        points.add(new BorderPoint(x, y, z, inclusive));
    }

    private boolean isNotVisible(int x, int y, int z, Location playerLocation) {
        return Math.sqrt(Math.pow(x - playerLocation.getX(), 2) + Math.pow(y - playerLocation.getY(), 2) + Math.pow(z - playerLocation.getZ(), 2)) > this.distance;
    }

}
