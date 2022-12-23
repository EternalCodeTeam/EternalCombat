package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.config.ConfigManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.util.UUID;

@Route(name = "combatlog")
public class ReloadCommand {

    private final ConfigManager configManager;
    private final NotificationAnnouncer announcer;
    private final MessageConfig messages;

    public ReloadCommand(ConfigManager configManager, NotificationAnnouncer announcer, MessageConfig messages) {
        this.configManager = configManager;
        this.announcer = announcer;
        this.messages = messages;
    }

    @Execute(route = "reload")
    @Permission("combatlog.reload")
    void execute(Player player) {
        UUID playerUniqueId = player.getUniqueId();

        this.configManager.reload();
        this.announcer.announceMessage(playerUniqueId, this.messages.reload);
    }
}
