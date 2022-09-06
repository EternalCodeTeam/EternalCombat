package com.eternalcode.combatlog.command.handler;

import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final MessageAnnouncer messageAnnouncer;
    private final MessageConfig messageConfig;

    public InvalidUsage(MessageAnnouncer messageAnnouncer, MessageConfig messageConfig) {
        this.messageAnnouncer = messageAnnouncer;
        this.messageConfig = messageConfig;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        Player player = (Player) sender;

        Formatter formatter = new Formatter();
        formatter.register("{COMMAND}", schematic.first());

        this.messageAnnouncer.sendMessage(player.getUniqueId(), formatter.format(this.messageConfig.invalidUsage));

    }

}
