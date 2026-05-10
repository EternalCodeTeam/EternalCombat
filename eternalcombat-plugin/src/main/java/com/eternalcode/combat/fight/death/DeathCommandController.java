package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.fight.event.FightUntagEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathCommandController implements Listener {

    private final DeathCommandService deathCommandService;
    private final Server server;

    public DeathCommandController(DeathCommandService deathCommandService, Server server) {
        this.deathCommandService = deathCommandService;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerUntag(FightUntagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());

        if (player == null) {
            return;
        }

        this.deathCommandService.handleUntag(player, event.getCause());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerDeath(PlayerDeathEvent event) {
        this.deathCommandService.handleDeath(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onPlayerRespawn(PlayerRespawnEvent event) {
        this.deathCommandService.handleRespawn(event.getPlayer());
    }
}
