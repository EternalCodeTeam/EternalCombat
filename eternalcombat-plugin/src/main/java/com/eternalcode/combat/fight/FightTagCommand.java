package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.MessagesSettings;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.notification.NoticeService;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.rollczi.litecommands.annotations.priority.Priority;
import dev.rollczi.litecommands.annotations.priority.PriorityValue;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "combatlog", aliases = "combat")
public class FightTagCommand {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;

    public FightTagCommand(FightManager fightManager, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
    }

    @Execute(name = "status")
    @Permission("eternalcombat.status")
    void status(@Context CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        this.noticeService.create()
            .notice(this.fightManager.isInCombat(targetUniqueId)
                ? this.config.messagesSettings.admin.playerInCombat
                : this.config.messagesSettings.admin.playerNotInCombat
            )
            .placeholder("{PLAYER}", target.getName())
            .viewer(sender)
            .send();

    }

    @Execute(name = "tag")
    @Permission("eternalcombat.tag")
    @Priority(PriorityValue.HIGH)
    void tag(@Context CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        Duration time = this.config.settings.combatTimerDuration;

        FightTagEvent event = this.fightManager.tag(targetUniqueId, time, CauseOfTag.COMMAND);

        if (event.isCancelled()) {
            CancelTagReason cancelReason = event.getCancelReason();

            this.tagoutReasonHandler(sender, cancelReason, this.config.messagesSettings);

            return;
        }

        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminTagPlayer)
            .placeholder("{PLAYER}", target.getName())
            .viewer(sender)
            .send();

    }

    @Execute(name = "tag")
    @Permission("eternalcombat.tag")
    void tagMultiple(@Context CommandSender sender, @Arg Player firstTarget, @Arg Player secondTarget) {
        Duration combatTime = this.config.settings.combatTimerDuration;
        MessagesSettings messagesSettings = this.config.messagesSettings;

        if (sender.equals(firstTarget) || sender.equals(secondTarget)) {

            this.noticeService.create()
                .notice(messagesSettings.admin.adminCannotTagSelf)
                .viewer(sender)
                .send();

            return;
        }

        FightTagEvent firstTagEvent = this.fightManager.tag(firstTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);
        FightTagEvent secondTagEvent = this.fightManager.tag(secondTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);

        if (firstTagEvent.isCancelled()) {
            CancelTagReason cancelReason = firstTagEvent.getCancelReason();

            this.tagoutReasonHandler(sender, cancelReason, messagesSettings);

            return;
        }

        if (secondTagEvent.isCancelled()) {
            CancelTagReason cancelReason = secondTagEvent.getCancelReason();

            this.tagoutReasonHandler(sender, cancelReason, messagesSettings);

            return;
        }

        if (firstTagEvent.isCancelled() && secondTagEvent.isCancelled()) {
            return;
        }

        this.noticeService.create()
            .notice(messagesSettings.admin.adminTagMultiplePlayers)
            .placeholder("{FIRST_PLAYER}", firstTarget.getName())
            .placeholder("{SECOND_PLAYER}", secondTarget.getName())
            .viewer(sender)
            .send();

    }

    @Execute(name = "untag")
    @Permission("eternalcombat.untag")
    void untag(@Context Player sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        if (!this.fightManager.isInCombat(targetUniqueId)) {
            this.noticeService.create()
                .viewer(sender)
                .notice(this.config.messagesSettings.admin.adminPlayerNotInCombat)
                .send();
            return;
        }

        FightUntagEvent event = this.fightManager.untag(targetUniqueId, CauseOfUnTag.COMMAND);
        if (event.isCancelled()) {
            return;
        }


        this.noticeService.create()
            .notice(this.config.messagesSettings.admin.adminUntagPlayer)
            .placeholder("{PLAYER}", target.getName())
            .viewer(sender)
            .send();
    }

    private void tagoutReasonHandler(CommandSender sender, CancelTagReason cancelReason, MessagesSettings messagesSettings) {
        if (cancelReason == CancelTagReason.TAGOUT) {
            this.noticeService.create()
                .notice(messagesSettings.admin.adminTagOutCanceled)
                .viewer(sender)
                .send();

        }
    }
}
