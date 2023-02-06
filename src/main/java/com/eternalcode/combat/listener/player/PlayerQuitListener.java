package com.eternalcode.combat.listener.player;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public PlayerQuitListener(CombatManager combatManager, PluginConfig config, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
        this.server = server;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatManager.getEnemy(player.getUniqueId()));

        if (enemy == null) {
            return;
        }

        this.combatManager.remove(enemy.getUniqueId());
        this.combatManager.remove(player.getUniqueId());

        UUID enemyUniqueId = enemy.getUniqueId();

        this.notificationAnnouncer.sendMessage(enemy, this.config.messages.unTagPlayer);
        this.combatManager.remove(enemyUniqueId);
        this.combatManager.remove(player.getUniqueId());

        player.setHealth(0.0);
    }
}
