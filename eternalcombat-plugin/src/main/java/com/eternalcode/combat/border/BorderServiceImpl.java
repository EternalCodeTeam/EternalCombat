package com.eternalcode.combat.border;

import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.combat.event.EventCaller;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.scheduler.Scheduler;
import dev.rollczi.litecommands.shared.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BorderServiceImpl implements BorderService {

    private final Scheduler scheduler;
    private final EventCaller eventCaller;

    private final BorderSettings settings;

    private final BorderTriggerIndex borderIndexes;
    private final BorderActivePointsIndex activeBorderIndex = new BorderActivePointsIndex();

    public BorderServiceImpl(Scheduler scheduler, Server server, RegionProvider provider, EventCaller eventCaller, BorderSettings settings) {
        this.scheduler = scheduler;
        this.eventCaller = eventCaller;
        this.settings = settings;
        this.borderIndexes = BorderTriggerIndex.started(server, scheduler, provider, settings);
    }

    @Override
    public void updateBorder(Player player, Location location) {
        Optional<BorderResult> result = resolveBorder(location);
        String world = player.getWorld().getName();

        if (result.isEmpty()) {
            if (this.activeBorderIndex.hasPoints(world, player.getUniqueId())) {
                return;
            }

            this.clearBorder(player);
            return;
        }

        scheduler.async(() -> {
            BorderResult borderResult = result.get();
            Set<BorderPoint> points = borderResult.collect();

            if (!points.isEmpty()) {
                BorderShowAsyncEvent event = eventCaller.publishEvent(new BorderShowAsyncEvent(player, points));
                points = event.getPoints();
            }

            Set<BorderPoint> toRemove = this.activeBorderIndex.putPoints(world, player.getUniqueId(), points);

            if (!toRemove.isEmpty()) {
                eventCaller.publishEvent(new BorderHideAsyncEvent(player, toRemove));
            }
        });
    }

    @Override
    public void clearBorder(Player player) {
        World world = player.getWorld();
        UUID uniqueId = player.getUniqueId();
        scheduler.async(() -> {
            Set<BorderPoint> borderPoints = this.activeBorderIndex.removePoints(world.getName(), uniqueId);
            if (!borderPoints.isEmpty()) {
                eventCaller.publishEvent(new BorderHideAsyncEvent(player, borderPoints));
            }
        });
    }

    @Override
    public Set<BorderPoint> getActiveBorder(Player player) {
        return this.activeBorderIndex.getPoints(player.getWorld().getName(), player.getUniqueId());
    }

    private Optional<BorderResult> resolveBorder(Location location) {
        LazyBorderResult result = new LazyBorderResult();
        List<BorderTrigger> triggered = borderIndexes.getTriggered(location);

        for (BorderTrigger trigger : triggered) {
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

        int realMinX = Math.max(borderMin.x(), x - settings.distanceRounded());
        int realMaxX = Math.min(borderMax.x(), x + settings.distanceRounded());
        int realMinY = Math.max(borderMin.y(), y - settings.distanceRounded());
        int realMaxY = Math.min(borderMax.y(), y + settings.distanceRounded());
        int realMinZ = Math.max(borderMin.z(), z - settings.distanceRounded());
        int realMaxZ = Math.min(borderMax.z(), z + settings.distanceRounded());

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
        if (isVisible(x, y, z, playerLocation)) {
            points.add(new BorderPoint(x, y, z, inclusive));
        }
    }

    private boolean isVisible(int x, int y, int z, Location player) {
        return Math.sqrt(Math.pow(x - player.getX(), 2) + Math.pow(y - player.getY(), 2) + Math.pow(z - player.getZ(), 2)) <= this.settings.distance();
    }

}
