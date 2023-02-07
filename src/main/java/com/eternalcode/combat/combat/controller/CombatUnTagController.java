package com.eternalcode.combat.combat.controller;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import panda.utilities.text.Formatter;

public class CombatUnTagController implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public CombatUnTagController(CombatManager combatManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.combatManager = combatManager;
        this.config = config;
        this.announcer = announcer;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        this.announcer.sendMessage(player, this.config.messages.unTagPlayer);
        this.combatManager.untag(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Formatter formatter = new Formatter()
            .register("{PLAYER}", player.getName());

        String format = formatter.format(this.config.messages.playerLoggedInCombat);

        this.combatManager.untag(player.getUniqueId());
        this.announcer.broadcast(player, format);
        player.setHealth(0.0);
    }

}
