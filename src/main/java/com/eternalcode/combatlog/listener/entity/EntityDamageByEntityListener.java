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

        this.combatManager.tag(player.getUniqueId(), enemy.getUniqueId(), combatTime);
        this.combatManager.tag(enemy.getUniqueId(), player.getUniqueId(), combatTime);

        this.notificationAnnouncer.sendMessage(player, this.messageConfig.tagPlayer);
        this.notificationAnnouncer.sendMessage(enemy, this.messageConfig.tagPlayer);
    }

}
