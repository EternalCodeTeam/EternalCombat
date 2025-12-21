package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightBypassCreativeController implements Listener {

    private final Server server;
    private final PluginConfig config;

    public FightBypassCreativeController(Server server, PluginConfig config) {
        this.server = server;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onFightTagEvent(FightTagEvent event) {
        UUID uniqueId = event.getPlayer();

        Player player = this.server.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        if (this.config.admin.excludeCreativePlayersFromCombat && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelReason(CancelTagReason.CREATIVE_MODE);
            event.setCancelled(true);
        }
    }

}
