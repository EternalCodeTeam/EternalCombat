package com.eternalcode.combat.bridge.lands;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LandsRegionController implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final LandsIntegration lands;

    public LandsRegionController(
        FightManager fightManager,
        NoticeService noticeService,
        LandsIntegration lands
    ) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.lands = lands;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || to.getBlockX() == from.getBlockX()
            && to.getBlockY() == from.getBlockY()
            && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        Area aFrom = this.lands.getArea(from);
        Area aTo = this.lands.getArea(to);

        if (aTo == null) {
            return;
        }
        if (aFrom != null && aFrom.getLand().equals(aTo.getLand())) {
            return;
        }

        event.setCancelled(true);
        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(notice -> notice.messagesSettings.cantEnterOnRegion)
            .send();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location to = event.getTo();
        if (to == null) {
            return;
        }

        Area area = this.lands.getArea(to);
        if (area != null) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(notice -> notice.messagesSettings.cantEnterOnRegion)
                .send();
        }
    }
}
