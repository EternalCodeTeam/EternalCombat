package com.eternalcode.combat.border;

import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class BorderUpdateController implements Listener {

    private final BorderService borderService;
    private final FightManager fightManager;
    private final Server server;

    public BorderUpdateController(BorderService borderService, FightManager fightManager, Server server) {
        this.borderService = borderService;
        this.fightManager = fightManager;
        this.server = server;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onMove(PlayerMoveEvent event) {
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
    void onFightEnd(FightUntagEvent event) {
        Player player = server.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        borderService.clearBorder(player);
    }

}
