package com.eripe14.combatlog.listeners.entity;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;

    public EntityDeathListener(CombatLogManager combatLogManager, MessageConfig messageConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity( ) instanceof Player player)) return;
        if (!this.combatLogManager.isInCombat(player.getUniqueId())) return;

        Player enemy = Bukkit.getPlayer(this.combatLogManager.getEnemy(player.getUniqueId()));

        if (enemy == null) return;

        enemy.sendMessage(ChatUtil.fixColor(this.messageConfig.unTagPlayer));

        this.combatLogManager.remove(player.getUniqueId());
        this.combatLogManager.remove(enemy.getUniqueId());
    }
}
