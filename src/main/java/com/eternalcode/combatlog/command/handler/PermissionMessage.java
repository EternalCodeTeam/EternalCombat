package com.eternalcode.combatlog.command.handler;

import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfig messageConfig;
    private final NotificationAnnouncer notificationAnnouncer;

    public PermissionMessage(MessageConfig messageConfig, NotificationAnnouncer notificationAnnouncer) {
        this.messageConfig = messageConfig;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        Player player = (Player) sender;

        this.notificationAnnouncer.sendMessage(player, this.messageConfig.noPermission);
    }

}
