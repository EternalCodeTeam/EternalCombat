package com.eternalcode.combatlog.command.handler;

import com.eternalcode.combatlog.NotificationAnnouncer;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;
import panda.utilities.text.Joiner;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfig messageConfig;
    private final NotificationAnnouncer notificationAnnouncer;

    public PermissionMessage(MessageConfig messageConfig, NotificationAnnouncer notificationAnnouncer) {
        this.messageConfig = messageConfig;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void handle(CommandSender commandSender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            String value = Joiner.on(", ")
                    .join(requiredPermissions.getPermissions())
                    .toString();

            Formatter formatter = new Formatter()
                    .register("{PERMISSION}", value);

            this.notificationAnnouncer.announceMessage(player.getUniqueId(), formatter.format(this.messageConfig.noPermission));
        }
    }

}