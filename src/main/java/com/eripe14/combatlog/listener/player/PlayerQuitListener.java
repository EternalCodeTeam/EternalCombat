package com.eripe14.combatlog.listener.player;

import com.eripe14.combatlog.combat.CombatManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public PlayerQuitListener(CombatManager combatManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
            return;
        }

        Player enemy = this.server.getPlayer(this.combatManager.getEnemy(player.getUniqueId()));

        if (enemy == null) {
            return;
        }

        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.unTagPlayer);

        combatManager.remove(enemy.getUniqueId());
        combatManager.remove(player.getUniqueId());

        player.setHealth(0.0);
    }
}
