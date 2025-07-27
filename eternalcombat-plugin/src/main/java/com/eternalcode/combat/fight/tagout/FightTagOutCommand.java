package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NoticeService;
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
    private final NoticeService noticeService;
    private final PluginConfig config;

    public FightTagOutCommand(
        FightTagOutService fightTagOutService,
        NoticeService noticeService,
        PluginConfig config
    ) {
        this.fightTagOutService = fightTagOutService;
        this.noticeService = noticeService;
        this.config = config;
    }

    @Execute
    void tagout(@Context Player sender, @Arg Duration time) {
        UUID targetUniqueId = sender.getUniqueId();

        this.fightTagOutService.tagOut(targetUniqueId, time);

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminTagOutSelf)
            .placeholder("{TIME}", DurationUtil.format(time))
            .viewer(sender)
            .send();

    }

    @Execute
    void tagout(@Context Player sender, @Arg Player target, @Arg Duration time) {
        UUID targetUniqueId = target.getUniqueId();

        this.fightTagOutService.tagOut(targetUniqueId, time);

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminTagOut)
            .placeholder("{PLAYER}", target.getName())
            .placeholder("{TIME}", DurationUtil.format(time))
            .viewer(sender)
            .send();

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.playerTagOut)
            .placeholder("{TIME}", DurationUtil.format(time))
            .player(target.getUniqueId())
            .send();

    }

    @Execute(name = "remove")
    void untagout(@Context Player sender, @Arg Player target) {
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
    void untagout(@Context Player sender) {
        UUID senderUniqueId = sender.getUniqueId();

        this.fightTagOutService.unTagOut(senderUniqueId);

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.playerTagOutOff)
            .viewer(sender)
            .send();

    }
}

