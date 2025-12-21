package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.FightTagEvent;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightBypassPermissionController implements Listener {

    private static final String BYPASS_PERMISSION = "eternalcombat.bypass";

    private final Server server;

    public FightBypassPermissionController(Server server) {
        this.server = server;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onFightTagEvent(FightTagEvent event) {
        UUID uniqueId = event.getPlayer();

        Player player = this.server.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        if (player.hasPermission(BYPASS_PERMISSION)) {
            event.setCancelReason(CancelTagReason.PERMISSION_BYPASS);
            event.setCancelled(true);
        }
    }
}
