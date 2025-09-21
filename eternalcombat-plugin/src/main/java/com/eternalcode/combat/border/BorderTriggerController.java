package com.eternalcode.combat.border;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import java.util.function.Supplier;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderTriggerController implements Listener {

    private final BorderService borderService;
    private final Supplier<BorderSettings> border;
    private final FightManager fightManager;
    private final Server server;

    public BorderTriggerController(BorderService borderService, Supplier<BorderSettings> border, FightManager fightManager, Server server) {
        this.borderService = borderService;
        this.border = border;
        this.fightManager = fightManager;
        this.server = server;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onMove(PlayerMoveEvent event) {
        if (!border.get().isEnabled()) {
            return;
        }

        Location to = event.getTo();
        Location from = event.getFrom();
        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (!fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        borderService.updateBorder(player, to);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onTeleport(PlayerTeleportEvent event) {
        if (!border.get().isEnabled()) {
            return;
        }

        Player player = event.getPlayer();
        if (!fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Location targetLocation = event.getTo();

        borderService.updateBorder(player, targetLocation);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onFightStart(FightTagEvent event) {
        if (!border.get().isEnabled()) {
            return;
        }

        Player player = server.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        // Sprawdź uprawnienia przed pokazaniem bordera
        borderService.updateBorder(player, player.getLocation());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onFightEnd(FightUntagEvent event) {
        if (!border.get().isEnabled()) {
            return;
        }

        Player player = server.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        borderService.clearBorder(player);
    }

}
