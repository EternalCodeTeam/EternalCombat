package com.eternalcode.combat.command;


import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

import java.util.List;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public InvalidUsage(PluginConfig config, NotificationAnnouncer announcer) {
        this.config = config;
        this.announcer = announcer;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, Schematic scheme) {
        List<String> schematics = scheme.getSchematics();

        for (String schematic : schematics) {
            Formatter formatter = new Formatter()
                .register("{USAGE}", schematic);

            this.announcer.send(commandSender, this.config.messages.invalidCommandUsage, formatter);
        }
    }
}
