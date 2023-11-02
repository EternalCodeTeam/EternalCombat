package com.eternalcode.combat.command;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;
import panda.utilities.text.Joiner;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public PermissionMessage(PluginConfig config, NotificationAnnouncer announcer) {
        this.config = config;
        this.announcer = announcer;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        String value = Joiner.on(", ")
            .join(requiredPermissions.getPermissions())
            .toString();

        Formatter formatter = new Formatter()
            .register("{PERMISSION}", value);

        this.announcer.send(commandSender, this.config.messages.noPermission, formatter);
    }

}
