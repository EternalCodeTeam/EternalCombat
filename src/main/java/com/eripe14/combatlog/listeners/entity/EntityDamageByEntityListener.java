package com.eripe14.combatlog.listeners.entity;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;

public class EntityDamageByEntityListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;

    public EntityDamageByEntityListener(CombatLogManager combatLogManager, MessageConfig messageConfig, PluginConfig pluginConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity( ) instanceof Player player) || !(event.getDamager( ) instanceof Player enemy)) return;

        this.combatLogManager.tag(player.getUniqueId(), enemy.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));
        this.combatLogManager.tag(enemy.getUniqueId(), player.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));

        player.sendMessage(ChatUtil.fixColor(this.messageConfig.tagPlayer));
        enemy.sendMessage(ChatUtil.fixColor(this.messageConfig.tagPlayer));
    }

}
