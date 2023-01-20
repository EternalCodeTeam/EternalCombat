package com.eternalcode.combat.command.implementation;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.config.ConfigManager;
import com.eternalcode.combat.config.implementation.MessageConfig;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.util.UUID;

@Route(name = "combatlog")
@Permission("eternalcombat.reload")
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
    void execute(Player player) {
        UUID playerUniqueId = player.getUniqueId();

        this.configManager.reload();
        this.announcer.announceMessage(playerUniqueId, this.messages.reload);
    }
}
