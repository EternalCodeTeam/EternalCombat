package com.eternalcode.combat.handler;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import dev.rollczi.litecommands.permission.MissingPermissionsHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MissingPermissionHandlerImpl implements MissingPermissionsHandler<CommandSender> {

    private final PluginConfig config;
    private final NotificationAnnouncer announcer;

    public MissingPermissionHandlerImpl(PluginConfig config, NotificationAnnouncer announcer) {
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

        if (invocation instanceof CommandSender sender) {

            if (sender instanceof Player player) {
                this.announcer.create()
                    .player(player.getUniqueId())
                    .notice(this.config.messages.noPermission)
                    .placeholder("{PERMISSION}", joinedText)
                    .send();
                return;
            }

            this.announcer.create()
                .console()
                .notice(this.config.messages.noPermission)
                .placeholder("{PERMISSION}", joinedText)
                .send();

        }

    }
}
