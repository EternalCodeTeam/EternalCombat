package com.eternalcode.combatlog.listener.entity;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.time.Duration;

public class EntityDamageByEntityListener implements Listener {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;
    private final MessageAnnouncer messageAnnouncer;

    public EntityDamageByEntityListener(CombatManager combatManager, MessageConfig messageConfig, PluginConfig pluginConfig, MessageAnnouncer messageAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
        this.messageAnnouncer = messageAnnouncer;
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


        this.combatManager.tag(player.getUniqueId(), enemy.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));
        this.combatManager.tag(enemy.getUniqueId(), player.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));

        this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.tagPlayer);
        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.tagPlayer);
    }

}
