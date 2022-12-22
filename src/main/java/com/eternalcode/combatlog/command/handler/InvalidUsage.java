package com.eternalcode.combatlog.command.handler;

import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfig messageConfig;

    public InvalidUsage(NotificationAnnouncer notificationAnnouncer, MessageConfig messageConfig) {
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfig = messageConfig;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        Player player = (Player) sender;

        Formatter formatter = new Formatter();
        formatter.register("{COMMAND}", schematic.first());

        this.notificationAnnouncer.sendMessage(player, formatter.format(this.messageConfig.invalidUsage));

    }

}
