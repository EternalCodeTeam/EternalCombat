package com.eripe14.combatlog.listeners.player;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {

    private final CombatLogManager combatLogManager;
    private final PluginConfig pluginConfig;
    private final MessageConfig messageConfig;

    public PlayerCommandPreprocessListener(CombatLogManager combatLogManager, PluginConfig pluginConfig, MessageConfig messageConfig) {
        this.combatLogManager = combatLogManager;
        this.pluginConfig = pluginConfig;
        this.messageConfig = messageConfig;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (!this.combatLogManager.isInCombat(player.getUniqueId())) return;

        final String command = event.getMessage();

        for (String blockedCommand : this.pluginConfig.blockedCommands) {
            if (!command.contains(blockedCommand)) return;

            event.setCancelled(true);

            player.sendMessage(ChatUtil.fixColor(this.messageConfig.cantUseCommand));
        }
    }
}
