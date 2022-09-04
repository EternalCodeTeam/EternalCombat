package com.eripe14.combatlog.listeners.player;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public PlayerQuitListener(CombatLogManager combatLogManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatLogManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatLogManager.getEnemy(player.getUniqueId()));

        if (enemy == null) {
            return;
        }

        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.unTagPlayer);

        combatLogManager.remove(enemy.getUniqueId());
        combatLogManager.remove(player.getUniqueId());

        player.setHealth(0.0);
    }
}
