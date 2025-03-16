package com.eternalcode.combat.fight.knockback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.bukkit.Location;
import org.bukkit.World;

class KnockbackOutsideRegionGenerator {

    private record Point2D(int x, int z) {

        Location toLocation(Location location) {
            World world = location.getWorld();
            int y = world.getHighestBlockYAt(x, z) + 1;

            return new Location(world, x, y, z, location.getYaw(), location.getPitch());
        }

        private static Point2D from(Location location) {
            return new Point2D(location.getBlockX(), location.getBlockZ());
        }

    }

    static Location generate(Location min, Location max, Location playerLocation) {
        NavigableMap<Double, List<Point2D>> points = generatePoints(Point2D.from(min), Point2D.from(max), Point2D.from(playerLocation));
        NavigableMap<Double, Double> distances = new TreeMap<>();
        double totalWeight = 0;

        Double maxDistance = points.lastKey();

        for (double distance : points.keySet()) {
            double weight = createWeight(distance, maxDistance);
            distances.put(distance, weight);
            totalWeight += weight;
        }

        double rand = Math.random() * totalWeight;
        double cumulativeWeight = 0;

        for (Map.Entry<Double, Double> entry : distances.entrySet()) {
            double distance = entry.getKey();
            double weight = entry.getValue();

            cumulativeWeight += weight;
            if (rand <= cumulativeWeight) {
                return getRandom(points.get(distance))
                    .toLocation(playerLocation);
            }
        }

        return getRandom(points.firstEntry().getValue())
            .toLocation(playerLocation);
    }

    private static Point2D getRandom(List<Point2D> points) {
        return points.get((int) (Math.random() * points.size()));
    }

    private static double createWeight(double distance, double maxDistance) {
        double last = Math.log(maxDistance);
        double weight = last - Math.log(distance);
        return Math.pow(weight, 10);
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
