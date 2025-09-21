package com.eternalcode.combat.fight.knockback;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.region.Region;
import com.eternalcode.combat.region.RegionProvider;
import com.eternalcode.combat.region.access.RegionEntryGuard;
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

    private final NoticeService noticeService;
    private final RegionProvider regionProvider;
    private final FightManager fightManager;
    private final KnockbackService knockbackService;
    private final Server server;
    private final RegionEntryGuard regionEntryGuard;

    public KnockbackRegionController(NoticeService noticeService, RegionProvider regionProvider, FightManager fightManager, KnockbackService knockbackService, Server server, RegionEntryGuard regionEntryGuard) {
        this.noticeService = noticeService;
        this.regionProvider = regionProvider;
        this.fightManager = fightManager;
        this.knockbackService = knockbackService;
        this.server = server;
        this.regionEntryGuard = regionEntryGuard;
    }

    private boolean canEnterDuringWar(Player player, Location targetLocation) {
        Optional<Region> regionOptional = this.regionProvider.getRegion(targetLocation);
        return regionOptional.filter(region -> this.regionEntryGuard.canEnter(player, region)).isPresent();
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

            // Sprawdź, czy gracz może wejść na teren podczas wojny
            if (this.canEnterDuringWar(player, locationTo)) {
                return; // Pozwól na wejście - państwa są w wojnie
            }

            Region region = regionOptional.get();
            if (region.contains(locationFrom)) {
                this.knockbackService.knockback(region, player);
                this.knockbackService.forceKnockbackLater(player, region);
            } else {
                event.setCancelled(true);
                this.knockbackService.knockbackLater(region, player, Duration.ofMillis(50));
            }

            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(config -> config.messagesSettings.cantEnterOnRegion)
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
            // Sprawdź, czy gracz może wejść na teren podczas wojny
            if (this.canEnterDuringWar(player, targetLocation)) {
                return; // Pozwól na teleport - państwa są w wojnie
            }

            event.setCancelled(true);
            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(config -> config.messagesSettings.cantEnterOnRegion)
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

        // Sprawdź, czy gracz może przebywać na terenie podczas wojny
        if (this.canEnterDuringWar(player, player.getLocation())) {
            return; // Pozwól na tagowanie - państwa są w wojnie
        }

        Region region = regionOptional.get();
        this.knockbackService.knockback(region, player);
        this.knockbackService.forceKnockbackLater(player, region);

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(config -> config.messagesSettings.cantEnterOnRegion)
            .send();
    }

}
