package com.eripe14.combatlog.listener.entity;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;

public class EntityDamageByEntityListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;
    private final MessageAnnouncer messageAnnouncer;

    public EntityDamageByEntityListener(CombatLogManager combatLogManager, MessageConfig messageConfig, PluginConfig pluginConfig, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!(event.getDamager() instanceof Player enemy)) {
            return;
        }

        this.combatLogManager.tag(player.getUniqueId(), enemy.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));
        this.combatLogManager.tag(enemy.getUniqueId(), player.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));

        this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.tagPlayer);
        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.tagPlayer);
    }

}
