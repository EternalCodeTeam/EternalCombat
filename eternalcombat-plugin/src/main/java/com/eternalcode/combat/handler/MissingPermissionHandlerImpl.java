package com.eternalcode.combat.handler;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NoticeService;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;

public class MissingPermissionHandlerImpl implements MissingPermissionsHandler<CommandSender> {

    private final PluginConfig config;
    private final NoticeService announcer;

    public MissingPermissionHandlerImpl(PluginConfig config, NoticeService announcer) {
        this.config = config;
        this.announcer = announcer;
    }

    @Override
    public void handle(
        Invocation<CommandSender> invocation,
        MissingPermissions missingPermissions,
        ResultHandlerChain<CommandSender> resultHandlerChain
    ) {
        String joinedText = missingPermissions.asJoinedText();


        this.announcer.create()
            .viewer(invocation.sender())
            .notice(this.config.messagesSettings.noPermission)
            .placeholder("{PERMISSION}", joinedText)
            .send();

    }
}

