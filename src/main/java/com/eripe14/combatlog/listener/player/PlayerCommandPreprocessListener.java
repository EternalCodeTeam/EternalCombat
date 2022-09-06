package com.eripe14.combatlog.listener.player;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final PluginConfig pluginConfig;
    private final MessageConfig messageConfig;
    private final MessageAnnouncer messageAnnouncer;

    public PlayerCommandPreprocessListener(CombatLogManager combatLogManager, PluginConfig pluginConfig, MessageConfig messageConfig, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!this.combatLogManager.isInCombat(player.getUniqueId())) {
            return;
        }

        String command = event.getMessage();

        for (String blockedCommand : this.pluginConfig.blockedCommands) {
            if (!command.contains(blockedCommand)) {
                return;
            }

            event.setCancelled(true);

            this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.cantUseCommand);
        }
    }
}
