package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.*;

public final class KnockbackService {

    private static final int FORCE_TELEPORT_MAX_ATTEMPTS = 10;
    private static final double KNOCKBACK_DAMPEN_FACTOR = 0.8;
    public static final int DEPTH_OF_SEARCHING = 10;

    private final PluginConfig config;
    private final MinecraftScheduler scheduler;
    private final RegionProvider regionProvider;

    private final Map<UUID, Region> insideRegion = new HashMap<>();

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

        Location currentLocation = player.getLocation();
        Vector shortestDirection = getShortestDirectionToEdge(region, currentLocation);

        // Reduces player's current velocity BEFORE applying knockback. Helps create a smoother and more consistent knockback.
        player.setVelocity(player.getVelocity().multiply(KNOCKBACK_DAMPEN_FACTOR));

        double y = Math.min(player.getVelocity().getY(), config.knockback.vertical);
        Vector velocity = shortestDirection.multiply(config.knockback.multiplier).setY(y);

        player.setFallDistance(0);
        player.setVelocity(velocity);
    }

    private Vector getShortestDirectionToEdge(Region region, Location current) {
        double directionToBottomEdge = current.getX() - region.getMin().getX();
        double directionToTopEdge = region.getMax().getX() - current.getX();
        double directionToLeftEdge = current.getZ() - region.getMin().getZ();
        double directionToRightEdge = region.getMax().getZ() - current.getZ();

        double shortestDirection = Math.min(
            Math.min(directionToBottomEdge, directionToTopEdge),
            Math.min(directionToLeftEdge, directionToRightEdge)
        );

        if (shortestDirection == directionToBottomEdge)
            return new Vector(-1, 0, 0);
        if (shortestDirection == directionToTopEdge)
            return new Vector(1, 0, 0);
        if (shortestDirection == directionToLeftEdge)
            return new Vector(0, 0, -1);
        if (shortestDirection == directionToRightEdge)
            return new Vector(0, 0, 1);
        throw new IllegalStateException("Failed to determine direction to edge");
    }

    public void forceKnockbackLater(Player player, Region region) {
        UUID playerId = player.getUniqueId();

        if (insideRegion.containsKey(playerId)) {
            return;
        }

        insideRegion.put(playerId, region);

        scheduler.runLater(player.getLocation(), () -> {
            insideRegion.remove(playerId);

            Location loc = player.getLocation();

            if (!region.contains(loc)) {
                return;
            }

            if (player.isInsideVehicle()) {
                player.leaveVehicle();
            }

            generate(player.getLocation(), Point2D.from(region.getMin()), Point2D.from(region.getMax()), 0)
                .ifPresent(location -> PaperLib.teleportAsync(player, location, TeleportCause.PLUGIN));
        }, config.knockback.forceTeleport.delay);
    }

    private Optional<Location> generate(Location playerLocation, Point2D min, Point2D max, int attempts) {
        if (attempts >= FORCE_TELEPORT_MAX_ATTEMPTS) {
            return Optional.of(playerLocation);
        }

        Optional<Location> location = KnockbackOutsideRegionGenerator.generate(min, max, playerLocation, candidate -> makeSafe(candidate));
        if (location.isEmpty()) {
            int expand = (attempts + 1) * (attempts + 1);
            return generate(playerLocation, min.expandMin(expand, expand), max.expandMax(expand, expand), attempts + 1);
        }

        Optional<Region> otherRegion = regionProvider.getRegion(location.get());
        if (otherRegion.isPresent()) {
            Region region = otherRegion.get();
            return generate(playerLocation, min.min(region.getMin()), max.max(region.getMax()), attempts + 1);
        }

        return location.map(loc -> loc.add(0.5, 0, 0.5));
    }

    private Optional<Location> makeSafe(Location random) {
        Location maybeSafe = random.clone();
        random.getWorld();
        int minY = maybeSafe.getBlockY() - DEPTH_OF_SEARCHING;

        CachedPillarOfBlocks pillar = new CachedPillarOfBlocks(maybeSafe.getBlockX(), maybeSafe.getBlockZ(), maybeSafe.getWorld());

        for (int y = maybeSafe.getBlockY(); y > minY; y--) {
            maybeSafe.setY(y);

            Material ground = pillar.get(maybeSafe.getBlockY() - 1);
            Material legs = pillar.get(maybeSafe.getBlockY());
            Material head = pillar.get(maybeSafe.getBlockY() + 1);

            if (ground.isSolid()
                && !config.knockback.forceTeleport.unsafeGroundBlocks.contains(ground)
                && config.knockback.forceTeleport.airBlocks.contains(legs)
                && config.knockback.forceTeleport.airBlocks.contains(head)
            ) {
                return Optional.of(maybeSafe);
            }
        }

        return Optional.empty();
    }

    private static class CachedPillarOfBlocks {

        private final int x;
        private final int z;
        private final World world;
        private final Map<Integer, Material> blocksTypes = new HashMap<>();

        private CachedPillarOfBlocks(int x, int z, World world) {
            this.x = x;
            this.z = z;
            this.world = world;
        }

        public Material get(int y) {
            return this.blocksTypes.computeIfAbsent(y, __ -> world.getType(x, y, z));
        }
    }

}
