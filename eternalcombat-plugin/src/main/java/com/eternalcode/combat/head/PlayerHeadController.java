package com.eternalcode.combat.head;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerHeadController implements Listener {

    private final PlayerHeadService playerHeadService;

    public PlayerHeadController(PlayerHeadService playerHeadService) {
        this.playerHeadService = playerHeadService;
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        this.playerHeadService.tryDropHead(event.getEntity(), event.getEntity().getKiller());
    }
} 