package com.eternalcode.combatlog.listener.player;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public PlayerQuitListener(CombatManager combatManager, MessageConfig messageConfig, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
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

        combatManager.remove(enemy.getUniqueId());
        combatManager.remove(player.getUniqueId());

        UUID enemyUniqueId = enemy.getUniqueId();
        this.notificationAnnouncer.announceMessage(enemyUniqueId, this.messageConfig.unTagPlayer);

        this.combatManager.remove(enemyUniqueId);
        this.combatManager.remove(player.getUniqueId());

        player.setHealth(0.0);
    }
}
