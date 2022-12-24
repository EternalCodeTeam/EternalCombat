package com.eternalcode.combatlog.listener.entity;

import com.eternalcode.combatlog.NotificationAnnouncer;
import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public EntityDeathListener(CombatManager combatManager, MessageConfig messageConfig, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.combatManager.isInCombat(playerUniqueId)) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatManager.getEnemy(playerUniqueId));

        if (enemy == null) {
            return;
        }

        UUID enemyUniqueId = enemy.getUniqueId();

        this.notificationAnnouncer.announceMessage(enemyUniqueId, this.messageConfig.unTagPlayer);

        this.combatManager.remove(playerUniqueId);
        this.combatManager.remove(enemyUniqueId);
    }
}
