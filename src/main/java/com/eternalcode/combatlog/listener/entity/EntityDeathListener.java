package com.eternalcode.combatlog.listener.entity;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatManager.getEnemy(player.getUniqueId()));

        if (enemy == null) {
            return;
        }

        this.notificationAnnouncer.sendMessage(enemy, this.messageConfig.unTagPlayer);

        this.combatManager.remove(player.getUniqueId());
        this.combatManager.remove(enemy.getUniqueId());
    }
}
