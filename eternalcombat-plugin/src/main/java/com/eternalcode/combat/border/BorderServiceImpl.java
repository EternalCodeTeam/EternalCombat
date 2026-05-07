package com.eternalcode.combat.border;

import com.eternalcode.combat.border.event.BorderHideAsyncEvent;
import com.eternalcode.combat.border.event.BorderShowAsyncEvent;
import com.eternalcode.combat.event.EventManager;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import com.eternalcode.commons.scheduler.Scheduler;
import dev.rollczi.litecommands.shared.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BorderServiceImpl implements BorderService {

    private final MinecraftScheduler scheduler;
    private final EventManager eventManager;

    private final Supplier<BorderSettings> settings;

    private final BorderTriggerIndex borderIndexes;
    private final BorderActivePointsIndex activeBorderIndex = new BorderActivePointsIndex();

    public BorderServiceImpl(MinecraftScheduler scheduler, Server server, RegionProvider provider, EventManager eventManager, Supplier<BorderSettings> settings) {
        this.scheduler = scheduler;
        this.eventManager = eventManager;
        this.settings = settings;
        this.borderIndexes = BorderTriggerIndex.started(server, scheduler, provider, settings);
    }

    @Override
    public void updateBorder(Player player, Location location) {
        Optional<BorderResult> result = resolveBorder(location);
        String world = player.getWorld().getName();

        if (result.isEmpty()) {
            if (!this.activeBorderIndex.hasPoints(world, player.getUniqueId())) {
                return;
            }

            this.clearBorder(player);
            return;
        }

        scheduler.runAsync(() -> {
            BorderResult borderResult = result.get();
            Set<BorderPoint> points = borderResult.collect();

            if (!points.isEmpty()) {
                BorderShowAsyncEvent event = eventManager.publishEvent(new BorderShowAsyncEvent(player, points));
                points = event.getPoints();
            }

            Set<BorderPoint> removed = this.activeBorderIndex.putPoints(world, player.getUniqueId(), points);

            if (!removed.isEmpty()) {
                eventManager.publishEvent(new BorderHideAsyncEvent(player, removed));
            }
        });
    }

    @Override
    public void clearBorder(Player player) {
        World world = player.getWorld();
        UUID uniqueId = player.getUniqueId();
        
        scheduler.runAsync(() -> {
            Set<BorderPoint> removed = this.activeBorderIndex.removePoints(world.getName(), uniqueId);
            if (!removed.isEmpty()) {
                eventManager.publishEvent(new BorderHideAsyncEvent(player, removed));
            }
        });
    }

    @Override
    public Set<BorderPoint> getActiveBorder(Player player) {
        return this.activeBorderIndex.getPoints(player.getWorld().getName(), player.getUniqueId());
    }

    private Optional<BorderResult> resolveBorder(Location location) {
        List<BorderTrigger> triggered = borderIndexes.getTriggered(location);

        if (triggered.isEmpty()) {
            return Optional.empty();
        }

        BorderLazyResult result = new BorderLazyResult();
        for (BorderTrigger trigger : triggered) {
            result.addLazyBorderPoints(new Lazy<>(() -> this.resolveBorderPoints(trigger, location)));
        }

        return Optional.of(result);
    }

    /* this code is ugly but is fast */
    private List<BorderPoint> resolveBorderPoints(BorderTrigger trigger, Location playerLocation) {
        BorderPoint borderMin = trigger.min();
        BorderPoint borderMax = trigger.max();

        int x = (int) Math.round(playerLocation.getX());
        int y = (int) Math.round(playerLocation.getY());
        int z = (int) Math.round(playerLocation.getZ());

        int distanceRounded = settings.get().distanceRounded();
        int realMinX = Math.max(borderMin.x(), x - distanceRounded);
        int realMaxX = Math.min(borderMax.x(), x + distanceRounded);
        int realMinY = Math.max(borderMin.y(), y - distanceRounded);
        int realMaxY = Math.min(borderMax.y(), y + distanceRounded);
        int realMinZ = Math.max(borderMin.z(), z - distanceRounded);
        int realMaxZ = Math.min(borderMax.z(), z + distanceRounded);

        List<BorderPoint> points = new ArrayList<>();

        if (borderMin.y() >= realMinY) { // Bottom wall
            for (int currentX = realMinX; currentX <= realMaxX - 1; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ - 1; currentZ++) {
                    addPoint(points, currentX, realMinY, currentZ, playerLocation, null);
                }
            }
        }

        if (borderMax.y() <= realMaxY) { // Top wall
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    BorderPoint innerPoint = new BorderPoint(Math.max(realMinX, currentX - 1), realMaxY - 1, Math.max(realMinZ, currentZ - 1));
                    addPoint(points, currentX, realMaxY, currentZ, playerLocation, innerPoint);
                }
            }
        }

        if (borderMin.x() >= realMinX) { // West wall (left)
            for (int currentY = realMinY; currentY <= realMaxY - 1; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ - 1; currentZ++) {
                    addPoint(points, realMinX, currentY, currentZ, playerLocation, null);
                }
            }
        }

        if (borderMax.x() <= realMaxX) { // East wall (right)
            for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                for (int currentZ = realMinZ; currentZ <= realMaxZ; currentZ++) {
                    BorderPoint innerPoint = new BorderPoint(realMaxX - 1, Math.max(realMinY, currentY - 1), Math.max(realMinZ, currentZ - 1));
                    addPoint(points, realMaxX, currentY, currentZ, playerLocation, innerPoint);
                }
            }
        }

        if (borderMin.z() >= realMinZ) { // North wall (front)
            for (int currentX = realMinX; currentX <= realMaxX - 1; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY - 1; currentY++) {
                    addPoint(points, currentX, currentY, realMinZ, playerLocation, null);
                }
            }
        }

        if (borderMax.z() <= realMaxZ) { // South wall (back)
            for (int currentX = realMinX; currentX <= realMaxX; currentX++) {
                for (int currentY = realMinY; currentY <= realMaxY; currentY++) {
                    BorderPoint innerPoint = new BorderPoint(Math.max(realMinX, currentX - 1), Math.max(realMinY, currentY - 1), realMaxZ - 1);
                    addPoint(points, currentX, currentY, realMaxZ, playerLocation, innerPoint);
                }
            }
        }

        return points;
    }

    private void addPoint(List<BorderPoint> points, int x, int y, int z, Location playerLocation, BorderPoint innerPoint) {
        if (isVisible(x, y, z, playerLocation)) {
            points.add(new BorderPoint(x, y, z, innerPoint));
        }
    }

    private boolean isVisible(int x, int y, int z, Location player) {
        return Math.sqrt(Math.pow(x - player.getX(), 2) + Math.pow(y - player.getY(), 2) + Math.pow(z - player.getZ(), 2)) <= this.settings.get().distance;
    }

}
