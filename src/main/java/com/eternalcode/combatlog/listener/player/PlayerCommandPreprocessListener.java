package com.eternalcode.combatlog.listener.player;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {

    private final CombatManager combatManager;
    private final PluginConfig pluginConfig;
    private final MessageConfig messageConfig;
    private final MessageAnnouncer messageAnnouncer;

    public PlayerCommandPreprocessListener(CombatManager combatManager, PluginConfig pluginConfig, MessageConfig messageConfig, MessageAnnouncer messageAnnouncer) {
        this.combatManager = combatManager;
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
        this.messageAnnouncer = messageAnnouncer;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!this.combatManager.isInCombat(player.getUniqueId())) {
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
