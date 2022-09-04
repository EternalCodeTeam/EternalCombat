package com.eripe14.combatlog.commands.handler;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.config.MessageConfig;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.command.permission.RequiredPermissions;
import dev.rollczi.litecommands.handle.PermissionHandler;
import org.bukkit.command.CommandSender;

public class PermissionMessage implements PermissionHandler<CommandSender> {

    private final MessageConfig messageConfig;

    public PermissionMessage(MessageConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, RequiredPermissions requiredPermissions) {
        sender.sendMessage(ChatUtil.fixColor(messageConfig.noPermission));
    }

}
