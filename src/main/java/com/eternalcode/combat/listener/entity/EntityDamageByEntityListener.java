package com.eternalcode.combat.listener.entity;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;
import java.util.UUID;

public class EntityDamageByEntityListener implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final NotificationAnnouncer notificationAnnouncer;

    public EntityDamageByEntityListener(CombatManager combatManager, PluginConfig config, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
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

        Duration combatTime = this.config.settings.combatLogTime;

        UUID enemyUniqueId = enemy.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        this.combatManager.tag(playerUniqueId, enemyUniqueId, combatTime);
        this.combatManager.tag(enemyUniqueId, playerUniqueId, combatTime);

        this.notificationAnnouncer.sendMessage(player, this.config.messages.tagPlayer);
        this.notificationAnnouncer.sendMessage(enemy, this.config.messages.tagPlayer);
    }
}
