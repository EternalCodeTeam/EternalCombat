package com.eternalcode.combat.handler;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NoticeService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;

public class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSender> {

    private final PluginConfig config;
    private final NoticeService announcer;

    public InvalidUsageHandlerImpl(PluginConfig config, NoticeService announcer) {
        this.config = config;
        this.announcer = announcer;
    }

    @Override
    public void handle(
        Invocation<CommandSender> invocation,
        InvalidUsage<CommandSender> commandSenderInvalidUsage,
        ResultHandlerChain<CommandSender> resultHandlerChain
    ) {
        Schematic schematic = commandSenderInvalidUsage.getSchematic();

        for (String usage : schematic.all()) {
            this.announcer.create()
                .viewer(invocation.sender())
                .notice(this.config.messagesSettings.invalidCommandUsage)
                .placeholder("{USAGE}", usage)
                .send();
        }

    }
}
