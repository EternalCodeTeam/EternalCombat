package com.eternalcode.combat.fight.death;

import com.eternalcode.combat.config.implementation.CommandSettings;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class DeathCommandExecutor {

    private final Server server;

    public DeathCommandExecutor(Server server) {
        this.server = server;
    }

    public void dispatch(CommandSettings.PostDeathSettings settings, Player player, String killerName) {
        String playerName = player.getName();
        
        this.dispatch(settings.console, this.server.getConsoleSender(), playerName, killerName);
        this.dispatch(settings.player, player, playerName, killerName);
    }

    public void dispatch(Collection<String> commands, CommandSender sender, String playerName, String killerName) {
        for (String command : commands) {
            String resolved = this.replacePlaceholders(command, playerName, killerName);
            this.server.dispatchCommand(sender, resolved);
        }
    }

    private String replacePlaceholders(String command, String playerName, String killerName) {
        return command
            .replace("{player}", playerName)
            .replace("{killer}", killerName);
    }
}

