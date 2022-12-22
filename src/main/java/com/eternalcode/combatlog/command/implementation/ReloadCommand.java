package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.config.ConfigManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

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
    @Permission("eternalcombatlog.reload")
    void execute(Player player) {
        this.configManager.reload();
        this.announcer.sendMessage(player, this.messages.reload);
    }
}
