package com.eternalcode.combat;

import com.eternalcode.combat.config.ConfigService;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.bossbar.FightBossBarService;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.async.Async;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.UUID;

@Route(name = "combatlog", aliases = "combat")
public class CombatCommand {

    private final FightManager fightManager;
    private final ConfigService configService;
    private final FightBossBarService bossBarService;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public CombatCommand(FightManager fightManager, ConfigService configService, FightBossBarService bossBarService, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightManager = fightManager;
        this.configService = configService;
        this.bossBarService = bossBarService;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute(route = "status", required = 1)
    @Permission("eternalcombat.status")
    void status(CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        PluginConfig.Messages messages = this.config.messages;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.announcer.sendMessage(sender, this.fightManager.isInCombat(targetUniqueId)
            ? formatter.format(messages.admin.playerInCombat)
            : formatter.format(messages.admin.playerNotInCombat));
    }

    @Execute(route = "tag", required = 1)
    @Permission("eternalcombat.tag")
    void tag(CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId(); 
        Duration time = this.config.settings.combatDuration;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.fightManager.tag(targetUniqueId, time, CauseOfTag.COMMAND);
        String format = formatter.format(this.config.messages.admin.adminTagPlayer);
        this.announcer.sendMessage(sender, format);
    }

    @Execute(route = "tag", required = 2)
    @Permission("eternalcombat.tag")
    void tagMultiple(CommandSender sender, @Arg Player firstTarget, @Arg Player secondTarget) {
        Duration combatTime = this.config.settings.combatDuration;
        PluginConfig.Messages messages = this.config.messages;
        
        if (sender.equals(firstTarget) || sender.equals(secondTarget)) {
            this.announcer.sendMessage(sender, messages.admin.adminCannotTagSelf);
            return;
        }
        
        boolean isTaggedFirst = this.fightManager.tag(firstTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);
        boolean isTaggedSecond = this.fightManager.tag(secondTarget.getUniqueId(), combatTime, CauseOfTag.COMMAND);

        Formatter formatter = new Formatter()
            .register("{FIRST_PLAYER}", firstTarget.getName())
            .register("{SECOND_PLAYER}", secondTarget.getName());

        String format = formatter.format(messages.admin.adminTagMultiplePlayers);

        if (isTaggedFirst) {
            this.announcer.sendMessage(firstTarget, messages.playerTagged);
        }
        else {
            this.announcer.sendMessage(sender, messages.admin.cannotTagPlayer);
        }

        if (isTaggedSecond) {
            this.announcer.sendMessage(secondTarget, messages.playerTagged);
        }
        else {
            this.announcer.sendMessage(sender, messages.admin.cannotTagPlayer);
        }

        if (isTaggedFirst || isTaggedSecond) {
            this.announcer.sendMessage(sender, format);
        }
    }

    @Async
    @Execute(route = "reload")
    @Permission("eternalcombat.reload")
    void execute(CommandSender player) {
        this.configService.reload();
        this.announcer.sendMessage(player, this.config.messages.admin.reload);
    }

    @Execute(route = "untag", required = 1)
    @Permission("eternalcombat.untag")
    void untag(Player sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        if (!this.fightManager.isInCombat(targetUniqueId)) {
            this.announcer.sendMessage(sender, this.config.messages.admin.adminPlayerNotInCombat);
            return;
        }

        if (!this.fightManager.untag(targetUniqueId, CauseOfUnTag.COMMAND)) {
            this.announcer.sendMessage(target, this.config.messages.admin.cannotTagPlayer);
            return;
        }

        this.announcer.sendMessage(target, this.config.messages.playerUntagged);
        this.bossBarService.hide(targetUniqueId);

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        String format = formatter.format(this.config.messages.admin.adminUntagPlayer);

        this.announcer.sendMessage(sender, format);
    }
}
