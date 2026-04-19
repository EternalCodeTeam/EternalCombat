package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import com.eternalcode.commons.scheduler.Task;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;

public final class KnockbackService {

    private final PluginConfig config;
    private final MinecraftScheduler scheduler;
    private final RegionProvider regionProvider;

    private final Map<UUID, Region> insideRegion = new HashMap<>();
    private final Set<UUID> fallbackActive = new HashSet<>();

    public KnockbackService(PluginConfig config, MinecraftScheduler scheduler, RegionProvider regionProvider) {
        this.config = config;
        this.scheduler = scheduler;
        this.regionProvider = regionProvider;
    }

    public void knockbackLater(Region region, Player player, Duration duration) {
        scheduler.runLater(() -> this.knockback(region, player), duration);
    }

    public void knockback(Region region, Player player) {

        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }

        Location loc = player.getLocation();
        Vector direction = getDirectionToEdge(region, loc);

        if (config.knockback.dampenVelocity) {
            player.setVelocity(player.getVelocity().multiply(config.knockback.dampenFactor));
        }

        boolean onGround = Math.abs(player.getVelocity().getY()) < 0.08;

        double y = onGround
            ? config.knockback.vertical
            : Math.min(player.getVelocity().getY(), config.knockback.maxAirVertical);

        Vector velocity = direction.multiply(config.knockback.multiplier).setY(y);

        player.setFallDistance(0);
        player.setVelocity(velocity);


        if (config.knockback.useTeleport) {
            scheduleSmartFallback(player, region);
        }
    }

    public void forceKnockbackLater(Player player, Region region) {
        UUID uuid = player.getUniqueId();

        if (insideRegion.containsKey(uuid)) {
            return;
        }

        insideRegion.put(uuid, region);

        scheduler.runLater(player.getLocation(), () -> {

            insideRegion.remove(uuid);

            Location loc = player.getLocation();
            double velocity = player.getVelocity().lengthSquared();

            if (velocity > 0.02) {
                return;
            }

            if (!region.contains(loc)) {
                return;
            }

            double distanceToEdge = getDistanceToEdge(region, loc);

            if (distanceToEdge > 1.5) {
                return;
            }

            if (fallbackActive.contains(uuid)) {
                return;
            }

            Location generated = generate(
                player.getLocation(),
                Point2D.from(region.getMin()),
                Point2D.from(region.getMax()),
                0
            );

            Location safe = makeSafe(generated);
            if (safe == null || safe.getWorld() == null) {
                return;
            }

            PaperLib.teleportAsync(player, safe.clone(), TeleportCause.PLUGIN);

        }, config.knockback.forceDelay);
    }

    private void scheduleSmartFallback(Player player, Region region) {
        UUID uuid = player.getUniqueId();

        if (fallbackActive.contains(uuid)) {
            return;
        }

        fallbackActive.add(uuid);

        final Task[] taskRef = new Task[1];

        taskRef[0] = scheduler.timer(() -> {

                Location check = player.getLocation();
                double velocity = player.getVelocity().lengthSquared();

                if (!region.contains(check)) {
                    fallbackActive.remove(uuid);
                    taskRef[0].cancel();
                    return;
                }

                if (velocity > 0.02) {
                    return;
                }

                Location generated = generate(
                    player.getLocation(),
                    Point2D.from(region.getMin()),
                    Point2D.from(region.getMax()),
                    0
                );

                Location safe = makeSafe(generated);
                if (safe == null || safe.getWorld() == null) {
                    fallbackActive.remove(uuid);
                    taskRef[0].cancel();
                    return;
                }

                PaperLib.teleportAsync(player, safe.clone(), TeleportCause.PLUGIN);

                fallbackActive.remove(uuid);
                taskRef[0].cancel();

            },
            Duration.ofMillis(100),
            Duration.ofMillis(100));
    }

    private Location makeSafe(Location loc) {
        if (loc == null || loc.getWorld() == null) return loc;

        return config.knockback.safeGroundCheck
            ? findSafeGround(loc)
            : loc.getWorld().getHighestBlockAt(loc).getLocation().add(0, config.knockback.groundOffset, 0);
    }

    private Location findSafeGround(Location loc) {

        if (loc.getWorld() == null) return loc;

        Location check = loc.clone();
        int minY = loc.getWorld().getMinHeight();

        for (int y = check.getBlockY(); y > minY; y--) {
            check.setY(y);

            Material type = check.getBlock().getType();
            Material above = check.clone().add(0, 1, 0).getBlock().getType();
            Material above2 = check.clone().add(0, 2, 0).getBlock().getType();

            if (type.isSolid()
                && !config.knockback.unsafeGroundBlocks.contains(type)
                && above.isAir()
                && above2.isAir()) {

                return check.clone().add(0, config.knockback.groundOffset, 0);
            }
        }

        return getSafeHighest(loc);
    }

    private Location getSafeHighest(Location loc) {
        if (loc == null || loc.getWorld() == null) return loc;

        if (!config.knockback.safeHighestFallback) {
            return loc.getWorld()
                .getHighestBlockAt(loc)
                .getLocation()
                .add(0, config.knockback.groundOffset, 0);
        }

        Location highest = loc.getWorld().getHighestBlockAt(loc).getLocation();
        int minY = loc.getWorld().getMinHeight();

        int startY = highest.getBlockY();
        int maxScan = config.knockback.safeHighestMaxScan;

        int endY = (maxScan < 0)
            ? minY
            : Math.max(minY, startY - maxScan);

        for (int y = startY; y > endY; y--) {
            highest.setY(y);

            Material type = highest.getBlock().getType();
            Material above = highest.clone().add(0, 1, 0).getBlock().getType();
            Material above2 = highest.clone().add(0, 2, 0).getBlock().getType();

            if (type.isSolid()
                && !config.knockback.unsafeGroundBlocks.contains(type)
                && above.isAir()
                && above2.isAir()) {

                return highest.clone().add(0, config.knockback.groundOffset, 0);
            }
        }

        return config.knockback.cancelIfNoSafeGround ? null : loc;
    }

    private Location generate(Location playerLocation, Point2D minX, Point2D maxX, int attempts) {
        if (attempts >= config.knockback.maxAttempts) {
            return playerLocation;
        }

        Location location = KnockbackOutsideRegionGenerator.generate(minX, maxX, playerLocation);

        Optional<Region> otherRegion = regionProvider.getRegion(location);
        if (otherRegion.isPresent()) {

            Region region = otherRegion.get();

            return generate(
                playerLocation,
                minX.min(region.getMin()),
                maxX.max(region.getMax()),
                attempts + 1
            );
        }

        return location;
    }

    private Vector getDirectionToEdge(Region region, Location loc) {
        double dxMin = loc.getX() - region.getMin().getX();
        double dxMax = region.getMax().getX() - loc.getX();
        double dzMin = loc.getZ() - region.getMin().getZ();
        double dzMax = region.getMax().getZ() - loc.getZ();

        double min = Math.min(Math.min(dxMin, dxMax), Math.min(dzMin, dzMax));

        if (Math.abs(min - dxMin) < 1e-6) return new Vector(-1, 0, 0);
        if (Math.abs(min - dxMax) < 1e-6) return new Vector(1, 0, 0);
        if (Math.abs(min - dzMin) < 1e-6) return new Vector(0, 0, -1);

        return new Vector(0, 0, 1);
    }

    private double getDistanceToEdge(Region region, Location loc) {
        double dxMin = loc.getX() - region.getMin().getX();
        double dxMax = region.getMax().getX() - loc.getX();
        double dzMin = loc.getZ() - region.getMin().getZ();
        double dzMax = region.getMax().getZ() - loc.getZ();

        return Math.min(Math.min(dxMin, dxMax), Math.min(dzMin, dzMax));
    }
}
