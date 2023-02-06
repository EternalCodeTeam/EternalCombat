package com.eternalcode.combat.listener.entity;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.UUID;

public class EntityDeathListener implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public EntityDeathListener(CombatManager combatManager, PluginConfig config, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
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

        this.notificationAnnouncer.sendMessage(enemy, this.config.messages.unTagPlayer);

        this.combatManager.remove(playerUniqueId);
        this.combatManager.remove(enemyUniqueId);
    }
}
