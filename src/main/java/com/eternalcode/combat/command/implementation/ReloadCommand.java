package com.eternalcode.combat.command.implementation;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.config.ConfigManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

@Route(name = "combatlog")
@Permission("eternalcombat.reload")
public class ReloadCommand {

    private final ConfigManager configManager;
    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public ReloadCommand(ConfigManager configManager, PluginConfig config, NotificationAnnouncer announcer) {
        this.configManager = configManager;
        this.config = config;
        this.announcer = announcer;
    }

    @Execute(route = "reload")
    void execute(Player player) {
        this.configManager.reload();
        this.announcer.sendMessage(player, this.config.messages.reload);
    }
}
