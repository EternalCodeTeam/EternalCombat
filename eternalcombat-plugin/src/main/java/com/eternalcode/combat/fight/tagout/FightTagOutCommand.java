package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.entity.Player;

@Permission("eternalcombat.tagout")
@Command(name = "tagout", aliases = "tagimmunity")
public class FightTagOutCommand {

    private final FightTagOutService fightTagOutService;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public FightTagOutCommand(
        FightTagOutService fightTagOutService,
        NotificationAnnouncer announcer,
        PluginConfig config
    ) {
        this.fightTagOutService = fightTagOutService;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute
    void tagout(@Context Player sender, @Arg Duration time) {
        UUID targetUniqueId = sender.getUniqueId();

        this.fightTagOutService.tagOut(targetUniqueId, time);

        this.announcer.create()
            .notice(this.config.messages.admin.adminTagOutSelf)
            .placeholder("{TIME}", DurationUtil.format(time))
            .viewer(sender)
            .send();

    }

    @Execute
    void tagout(@Context Player sender, @Arg Player target, @Arg Duration time) {
        UUID targetUniqueId = target.getUniqueId();

        this.fightTagOutService.tagOut(targetUniqueId, time);

        this.announcer.create()
            .notice(this.config.messages.admin.adminTagOut)
            .placeholder("{PLAYER}", target.getName())
            .placeholder("{TIME}", DurationUtil.format(time))
            .viewer(sender)
            .send();

        this.announcer.create()
            .notice(this.config.messages.admin.playerTagOut)
            .placeholder("{TIME}", DurationUtil.format(time))
            .player(target.getUniqueId())
            .send();

    }

    @Execute(name = "remove")
    void untagout(@Context Player sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        this.fightTagOutService.unTagOut(targetUniqueId);


        if (!targetUniqueId.equals(sender.getUniqueId())) {
            this.announcer.create()
                .notice(this.config.messages.admin.adminTagOutOff)
                .placeholder("{PLAYER}", target.getName())
                .viewer(sender)
                .send();
        }

        this.announcer.create()
            .notice(this.config.messages.admin.playerTagOutOff)
            .player(targetUniqueId)
            .send();
    }

    @Execute(name = "remove")
    void untagout(@Context Player sender) {
        UUID senderUniqueId = sender.getUniqueId();

        this.fightTagOutService.unTagOut(senderUniqueId);

        this.announcer.create()
            .notice(this.config.messages.admin.playerTagOutOff)
            .viewer(sender)
            .send();

    }
}

