package com.eternalcode.combat.combat.controller;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatUnTagController implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final NotificationAnnouncer notificationAnnouncer;

    public CombatUnTagController(CombatManager combatManager, PluginConfig config, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        this.notificationAnnouncer.sendMessage(player, this.config.messages.unTagPlayer);
        this.combatManager.untag(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        this.combatManager.untag(player.getUniqueId());
        player.setHealth(0.0);
    }

}
