package com.eripe14.combatlog.commands.handler;

import com.eripe14.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.command.LiteInvocation;
import dev.rollczi.litecommands.handle.InvalidUsageHandler;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InvalidUsage implements InvalidUsageHandler<CommandSender> {

    private final MessageAnnouncer messageAnnouncer;

    public InvalidUsage(MessageAnnouncer messageAnnouncer) {
        this.messageAnnouncer = messageAnnouncer;
    }

    @Override
    public void handle(CommandSender sender, LiteInvocation invocation, Schematic schematic) {
        List<String> schematics = schematic.getSchematics();

        Player player = (Player) sender;

        if (schematics.size() == 1) {
            this.messageAnnouncer.sendMessage(player.getUniqueId(), "&4Nie poprawne użycie komendy &8>> &7" + schematics.get(0));
            return;
        }

        this.messageAnnouncer.sendMessage(player.getUniqueId(), "&cNie poprawne użycie komendy!");

        for (String schema : schematics) {
            this.messageAnnouncer.sendMessage(player.getUniqueId(), "&8 >> &7" + schema);
        }
    }

}
