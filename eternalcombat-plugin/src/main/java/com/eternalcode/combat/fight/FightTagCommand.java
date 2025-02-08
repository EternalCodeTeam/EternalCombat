package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CancelTagReason;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.notification.NotificationAnnouncer;
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
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public FightTagCommand(FightManager fightManager, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightManager = fightManager;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute(name = "status")
    @Permission("eternalcombat.status")
    void status(@Context CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        this.announcer.create()
            .notice(this.fightManager.isInCombat(targetUniqueId)
                ? this.config.messages.admin.playerInCombat
                : this.config.messages.admin.playerNotInCombat
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

            this.tagoutReasonHandler(sender, cancelReason, this.config.messages);

            return;
        }

        this.announcer.create()
            .notice(this.config.messages.admin.adminTagPlayer)
            .placeholder("{PLAYER}", target.getName())
            .viewer(sender)
            .send();

    }

    @Execute(name = "tag")
    @Permission("eternalcombat.tag")
    void tagMultiple(@Context CommandSender sender, @Arg Player firstTarget, @Arg Player secondTarget) {
        Duration combatTime = this.config.settings.combatTimerDuration;
        PluginConfig.Messages messages = this.config.messages;

        if (sender.equals(firstTarget) || sender.equals(secondTarget)) {

            this.announcer.create()
                .notice(messages.admin.adminCannotTagSelf)
                .viewer(sender)
                .send();

            return;
        }

        FightTagEvent firstTagEvent = this.fightManager.tag(firstTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);
        FightTagEvent secondTagEvent = this.fightManager.tag(secondTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);

        if (firstTagEvent.isCancelled()) {
            CancelTagReason cancelReason = firstTagEvent.getCancelReason();

            this.tagoutReasonHandler(sender, cancelReason, messages);

            return;
        }

        if (secondTagEvent.isCancelled()) {
            CancelTagReason cancelReason = secondTagEvent.getCancelReason();

            this.tagoutReasonHandler(sender, cancelReason, messages);

            return;
        }

        if (firstTagEvent.isCancelled() && secondTagEvent.isCancelled()) {
            return;
        }

        this.announcer.create()
            .notice(messages.admin.adminTagMultiplePlayers)
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
            this.announcer.create()
                .viewer(sender)
                .notice(this.config.messages.admin.adminPlayerNotInCombat)
                .send();
            return;
        }

        FightUntagEvent event = this.fightManager.untag(targetUniqueId, CauseOfUnTag.COMMAND);
        if (event.isCancelled()) {
            return;
        }


        this.announcer.create()
            .notice(this.config.messages.admin.adminUntagPlayer)
            .placeholder("{PLAYER}", target.getName())
            .viewer(sender)
            .send();
    }

    private void tagoutReasonHandler(CommandSender sender, CancelTagReason cancelReason, PluginConfig.Messages messages) {
        if (cancelReason == CancelTagReason.TAGOUT) {
            this.announcer.create()
                .notice(messages.admin.adminTagOutCanceled)
                .viewer(sender)
                .send();

        }
    }
}
