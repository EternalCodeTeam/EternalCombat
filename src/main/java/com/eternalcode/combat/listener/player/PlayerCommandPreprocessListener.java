package com.eternalcode.combat.listener.player;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class PlayerCommandPreprocessListener implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final NotificationAnnouncer notificationAnnouncer;

    public PlayerCommandPreprocessListener(CombatManager combatManager, PluginConfig config, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.config = config;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.combatManager.isInCombat(playerUniqueId)) {
            return;
        }

        String command = event.getMessage();

        for (String blockedCommand : this.config.settings.blockedCommands) {
            if (!command.contains(blockedCommand)) {
                return;
            }

            event.setCancelled(true);

            this.notificationAnnouncer.sendMessage(player, this.config.messages.cantUseCommand);
        }
    }
}
