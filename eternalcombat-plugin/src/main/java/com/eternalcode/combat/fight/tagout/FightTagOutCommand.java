package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.time.DurationService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

@Permission("eternalcombat.tagout")
@Command(name = "tagout", aliases = "tagimmunity")
public class FightTagOutCommand {

    private final FightTagOutService fightTagOutService;
    private final NoticeService noticeService;
    private final DurationService durationService;
    private final PluginConfig config;

    public FightTagOutCommand(
        FightTagOutService fightTagOutService,
        NoticeService noticeService, DurationService durationService,
        PluginConfig config
    ) {
        this.fightTagOutService = fightTagOutService;
        this.noticeService = noticeService;
        this.durationService = durationService;
        this.config = config;
    }

    @Execute
    void tagOut(@Context Player sender, @Arg Duration time) {
        this.fightTagOutService.tagOut(sender.getUniqueId(), time);

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminTagOutSelf)
            .placeholder("{TIME}", durationService.format(time))
            .viewer(sender)
            .send();

    }

    @Execute
    void tagout(@Context Player sender, @Arg Player target, @Arg Duration time) {
        this.fightTagOutService.tagOut(target.getUniqueId(), time);

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminTagOut)
            .placeholder("{PLAYER}", target.getName())
            .placeholder("{TIME}", durationService.format(time))
            .viewer(sender)
            .send();

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.playerTagOut)
            .placeholder("{TIME}", durationService.format(time))
            .player(target.getUniqueId())
            .send();

    }

    @Execute(name = "remove")
    void unTagOut(@Context Player sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        this.fightTagOutService.unTagOut(targetUniqueId);

        if (!targetUniqueId.equals(sender.getUniqueId())) {
            this.noticeService.create()
                .notice(this.config.messagesSettings.admin.adminTagOutOff)
                .placeholder("{PLAYER}", target.getName())
                .viewer(sender)
                .send();
        }

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.playerTagOutOff)
            .player(targetUniqueId)
            .send();
    }

    @Execute(name = "remove")
    void unTagout(@Context Player sender) {
        this.fightTagOutService.unTagOut(sender.getUniqueId());

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.playerTagOutOff)
            .viewer(sender)
            .send();

    }
}

