package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import java.time.Duration;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class KnockbackRegionController implements Listener {

    private final NotificationAnnouncer announcer;
    private final RegionProvider regionProvider;
    private final FightManager fightManager;
    private final KnockbackService knockbackService;
    private final Server server;

    public KnockbackRegionController(NotificationAnnouncer announcer, RegionProvider regionProvider, FightManager fightManager, KnockbackService knockbackService, Server server) {
        this.announcer = announcer;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
        this.knockbackService = knockbackService;
        this.server = server;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location locationTo = event.getTo();
        int xTo = locationTo.getBlockX();
        int yTo = locationTo.getBlockY();
        int zTo = locationTo.getBlockZ();

        Location locationFrom = event.getFrom();
        int xFrom = locationFrom.getBlockX();
        int yFrom = locationFrom.getBlockY();
        int zFrom = locationFrom.getBlockZ();

        if (xTo != xFrom || yTo != yFrom || zTo != zFrom) {
            Optional<Region> regionOptional = this.regionProvider.getRegion(locationTo);
            if (regionOptional.isEmpty()) {
                return;
            }

            Region region = regionOptional.get();
            if (region.contains(locationFrom)) {
                this.knockbackService.knockback(region, player);
                this.knockbackService.forceKnockbackLater(player, region);
            } else {
                event.setCancelled(true);
                this.knockbackService.knockbackLater(region, player, Duration.ofMillis(50));
            }

            this.announcer.create()
                .player(player.getUniqueId())
                .notice(config -> config.messages.cantEnterOnRegion)
                .send();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location targetLocation = event.getTo();

        if (this.regionProvider.isInRegion(targetLocation)) {
            event.setCancelled(true);
            this.announcer.create()
                .player(player.getUniqueId())
                .notice(config -> config.messages.cantEnterOnRegion)
                .send();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onTag(FightTagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());
        if (player == null) {
            throw new IllegalStateException("Player cannot be null!");
        }

        Optional<Region> regionOptional = this.regionProvider.getRegion(player.getLocation());
        if (regionOptional.isEmpty()) {
            return;
        }

        Region region = regionOptional.get();
        this.knockbackService.knockback(region, player);
        this.knockbackService.forceKnockbackLater(player, region);

        this.announcer.create()
            .player(player.getUniqueId())
            .notice(config -> config.messages.cantEnterOnRegion)
            .send();
    }

}
