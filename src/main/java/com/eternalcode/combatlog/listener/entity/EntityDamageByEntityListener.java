package com.eternalcode.combatlog.listener.entity;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;
import java.util.UUID;

public class EntityDamageByEntityListener implements Listener {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;
    private final NotificationAnnouncer notificationAnnouncer;

    public EntityDamageByEntityListener(CombatManager combatManager, MessageConfig messageConfig, PluginConfig pluginConfig, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Player enemy = (Player) event.getDamager();

        Duration combatTime = this.pluginConfig.combatLogTime;

        UUID enemyUniqueId = enemy.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        this.combatManager.tag(playerUniqueId, enemyUniqueId, combatTime);
        this.combatManager.tag(enemyUniqueId, playerUniqueId, combatTime);

        this.notificationAnnouncer.announceMessage(playerUniqueId, this.messageConfig.tagPlayer);
        this.notificationAnnouncer.announceMessage(enemyUniqueId, this.messageConfig.tagPlayer);
    }

}
