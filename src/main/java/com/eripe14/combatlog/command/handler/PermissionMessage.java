package com.eripe14.combatlog.command.handler;

import com.eripe14.combatlog.config.implementation.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfig messageConfig;
    private final MessageAnnouncer messageAnnouncer;

    public PermissionMessage(MessageConfig messageConfig, MessageAnnouncer messageAnnouncer) {
        this.messageConfig = messageConfig;
        this.messageAnnouncer = messageAnnouncer;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        Player player = (Player) sender;

        this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.noPermission);
    }

}
