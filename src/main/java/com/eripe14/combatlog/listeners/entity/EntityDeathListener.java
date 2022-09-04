package com.eripe14.combatlog.listeners.entity;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public EntityDeathListener(CombatLogManager combatLogManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!this.combatLogManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatLogManager.getEnemy(player.getUniqueId()));

        if (enemy == null) {
            return;
        }

        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.unTagPlayer);

        this.combatLogManager.remove(player.getUniqueId());
        this.combatLogManager.remove(enemy.getUniqueId());
    }
}
