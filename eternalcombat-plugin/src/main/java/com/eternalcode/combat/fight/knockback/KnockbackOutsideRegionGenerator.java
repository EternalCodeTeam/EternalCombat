package com.eternalcode.combat.fight.knockback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import org.bukkit.Location;

class KnockbackOutsideRegionGenerator {

    static Optional<Location> generate(Point2D min, Point2D max, Location playerLocation, Function<Location, Optional<Location>> safeMapper) {
        return generatePoints(min, max, Point2D.from(playerLocation)).entrySet().stream()
            .flatMap(entry -> entry.getValue().stream())
            .map(point2D -> point2D.toLocation(playerLocation))
            .flatMap(safeMapper.andThen(validPoint -> validPoint.stream()))
            .findFirst();
    }

    private static NavigableMap<Double, List<Point2D>> generatePoints(Point2D min, Point2D max, Point2D location) {
        Set<Point2D> points = new HashSet<>();

        for (int i = min.x() - 1; i <= max.x() + 1; i++) {
            points.add(new Point2D(i, min.z() - 1));
            points.add(new Point2D(i, max.z() + 1));
        }
        for (int i = min.z(); i <= max.z(); i++) {
            points.add(new Point2D(min.x() - 1, i));
            points.add(new Point2D(max.x() + 1, i));
        }

        TreeMap<Double, List<Point2D>> result = new TreeMap<>();

        for (Point2D point : points) {
            double distance = distance(location, point);
            result.computeIfAbsent(distance, k -> new ArrayList<>()).add(point);
        }

        return result;
    }

    private static double distance(Point2D p1, Point2D p2) {
        return Math.sqrt(Math.pow(p2.x() - p1.x(), 2) + Math.pow(p2.z() - p1.z(), 2));
    }

}
