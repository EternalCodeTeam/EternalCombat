package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.region.Point;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.commons.bukkit.scheduler.MinecraftScheduler;
import io.papermc.lib.PaperLib;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public final class KnockbackService {

    private final PluginConfig config;
    private final FightManager fightManager;
    private final MinecraftScheduler scheduler;
    private final RegionProvider regionProvider;
    private final Server server;

    private final Map<UUID, Region> insideRegion = new HashMap<>();

    public KnockbackService(
        PluginConfig config,
        FightManager fightManager,
        MinecraftScheduler scheduler,
        RegionProvider regionProvider,
        Server server
    ) {
        this.config = config;
        this.fightManager = fightManager;
        this.scheduler = scheduler;
        this.regionProvider = regionProvider;
        this.server = server;
    }

    public void knockbackLater(Region region, Player player, Duration duration) {
        UUID uniqueId = player.getUniqueId();
        this.scheduler.runLater(() -> {
            Player onlinePlayer = this.server.getPlayer(uniqueId);
            if (onlinePlayer == null || !this.fightManager.isInCombat(uniqueId) || onlinePlayer.isDead()) {
                return;
            }

            this.knockback(region, onlinePlayer);
        }, duration);
    }

    public void forceKnockbackLater(Player player, Region region) {
        if (insideRegion.containsKey(player.getUniqueId())) {
            return;
        }

        insideRegion.put(player.getUniqueId(), region);

        scheduler.runLater(
            player.getLocation(), () -> {
                UUID uniqueId = player.getUniqueId();
                insideRegion.remove(uniqueId);

                Player onlinePlayer = this.server.getPlayer(uniqueId);
                if (onlinePlayer == null || !this.fightManager.isInCombat(uniqueId) || onlinePlayer.isDead()) {
                    return;
                }

                Location playerLocation = onlinePlayer.getLocation();
                if (!region.contains(playerLocation) && !regionProvider.isInRegion(playerLocation)) {
                    return;
                }

                Location location =
                    generate(playerLocation, Point2D.from(region.getMin()), Point2D.from(region.getMax()));

                PaperLib.teleportAsync(onlinePlayer, location, TeleportCause.PLUGIN);
            }, this.config.knockback.forceDelay);
    }

    private Location generate(Location playerLocation, Point2D minX, Point2D maxX) {
        Location location = KnockbackOutsideRegionGenerator.generate(minX, maxX, playerLocation);
        Optional<Region> otherRegion = regionProvider.getRegion(location);
        if (otherRegion.isPresent()) {
            Region region = otherRegion.get();
            return generate(playerLocation, minX.min(region.getMin()), maxX.max(region.getMax()));
        }

        return location;
    }

    public void knockback(Region region, Player player) {
        Point point = region.getCenter();
        Location subtract = player.getLocation().subtract(point.x(), 0, point.z());

        Vector knockbackVector = new Vector(subtract.getX(), 0, subtract.getZ()).normalize();
        double multiplier = this.config.knockback.multiplier;
        Vector configuredVector = new Vector(multiplier, 0.5, multiplier);

        player.setVelocity(knockbackVector.multiply(configuredVector));
    }
}
