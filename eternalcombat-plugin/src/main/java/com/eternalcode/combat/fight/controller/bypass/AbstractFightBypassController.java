package com.eternalcode.combat.fight.controller.bypass;

import com.eternalcode.combat.fight.event.FightTagEvent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public abstract class AbstractFightBypassController implements Listener {

    private final Server server;

    protected AbstractFightBypassController(Server server) {
        this.server = server;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public final void onFightTagEvent(FightTagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());
        if (player == null) {
            return;
        }

        this.handleBypass(event, player);
    }

    protected abstract void handleBypass(FightTagEvent event, Player player);

}
