package com.eripe14.combatlog.listeners.player;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;

    public PlayerQuitListener(CombatLogManager combatLogManager, MessageConfig messageConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatLogManager.isInCombat(player.getUniqueId())) return;

        Player enemy = Bukkit.getPlayer(this.combatLogManager.getEnemy(player.getUniqueId()));

        if (enemy == null) return;

        enemy.sendMessage(ChatUtil.fixColor(this.messageConfig.unTagPlayer));

        combatLogManager.remove(enemy.getUniqueId());
        combatLogManager.remove(player.getUniqueId());

        player.setHealth(0.0);
    }
}
