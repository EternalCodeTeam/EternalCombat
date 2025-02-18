package com.eternalcode.combat.border;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class BorderActivePointsIndex {

    private final Map<String, Map<UUID, Set<BorderPoint>>> registry = new ConcurrentHashMap<>();

    boolean hasPoints(String world, UUID player) {
        Map<UUID, Set<BorderPoint>> worldRegistry = this.registry.get(world);
        if (worldRegistry == null) {
            return false;
        }

        return worldRegistry.containsKey(player);
    }

    Set<BorderPoint> putPoints(String world, UUID player, Set<BorderPoint> points) {
        Map<UUID, Set<BorderPoint>> worldRegistry = this.registry.computeIfAbsent(world, k -> new ConcurrentHashMap<>());
        Set<BorderPoint> oldPoints = worldRegistry.put(player, points);
        if (oldPoints != null) {
            return calculateRemovedPoints(points, oldPoints);
        }

        return Set.of();
    }

    private static Set<BorderPoint> calculateRemovedPoints(Set<BorderPoint> points, Set<BorderPoint> oldPoints) {
        Set<BorderPoint> removed = new HashSet<>();
        for (BorderPoint oldPoint : oldPoints) {
            if (!points.contains(oldPoint)) {
                removed.add(oldPoint);
            }
        }
        return Collections.unmodifiableSet(removed);
    }

    Set<BorderPoint> getPoints(String world, UUID player) {
        Map<UUID, Set<BorderPoint>> worldRegistry = this.registry.get(world);
        if (worldRegistry == null) {
            return Set.of();
        }

        return worldRegistry.getOrDefault(player, Set.of());
    }

    Set<BorderPoint> removePoints(String world, UUID player) {
        Map<UUID, Set<BorderPoint>> worldRegistry = this.registry.get(world);
        if (worldRegistry == null) {
            return Set.of();
        }

        Set<BorderPoint> remove = worldRegistry.remove(player);
        if (remove == null) {
            return Set.of();
        }

        return remove;
    }

}
