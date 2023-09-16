package com.eternalcode.combat;

import com.eternalcode.combat.config.ConfigService;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
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
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public CombatCommand(FightManager fightManager, ConfigService configService, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightManager = fightManager;
        this.configService = configService;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute(route = "status", required = 1)
    @Permission("eternalcombat.status")
    void status(CommandSender player, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        PluginConfig.Messages messages = this.config.messages;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.announcer.sendMessage(player, this.fightManager.isInCombat(targetUniqueId)
            ? formatter.format(messages.admin.playerInCombat)
            : formatter.format(messages.admin.playerNotInCombat));
    }

    @Execute(route = "tag", required = 1)
    @Permission("eternalcombat.tag")
    void tag(Player player, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        Duration time = this.config.settings.combatDuration;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.fightManager.tag(targetUniqueId, time);

        String format = formatter.format(this.config.messages.admin.adminTagPlayer);
        this.announcer.sendMessage(player, format);
    }

    @Execute(route = "tag", required = 2)
    @Permission("eternalcombat.tag")
    public void tagMultiple(Player player, @Arg Player firstTarget, @Arg Player secondTarget) {
        Duration combatTime = this.config.settings.combatDuration;
        PluginConfig.Messages messages = this.config.messages;

        UUID firstTargetUniqueId = firstTarget.getUniqueId();
        UUID secondTargetUniqueId = secondTarget.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        if (playerUniqueId.equals(firstTargetUniqueId) || playerUniqueId.equals(secondTargetUniqueId)) {
            this.announcer.sendMessage(player, messages.admin.adminCannotTagSelf);
            return;
        }

        this.fightManager.tag(firstTargetUniqueId, combatTime);
        this.fightManager.tag(secondTargetUniqueId, combatTime);

        Formatter formatter = new Formatter()
            .register("{FIRST_PLAYER}", firstTarget.getName())
            .register("{SECOND_PLAYER}", secondTarget.getName());

        String format = formatter.format(messages.admin.adminTagMultiplePlayers);
        this.announcer.sendMessage(player, format);

        this.announcer.sendMessage(firstTarget, messages.playerTagged);
        this.announcer.sendMessage(secondTarget, messages.playerTagged);
    }

    @Execute(route = "untag", required = 1)
    @Permission("eternalcombat.untag")
    void untag(Player player, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        if (!this.fightManager.isInCombat(targetUniqueId)) {
            this.announcer.sendMessage(player, this.config.messages.admin.adminPlayerNotInCombat);
            return;
        }

        this.announcer.sendMessage(target, this.config.messages.playerUntagged);
        this.fightManager.untag(targetUniqueId);

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        String format = formatter.format(this.config.messages.admin.adminUntagPlayer);

        this.announcer.sendMessage(player, format);
    }

    @Async
    @Execute(route = "reload")
    @Permission("eternalcombat.reload")
    void execute(CommandSender player) {
        this.configService.reload();
        this.announcer.sendMessage(player, this.config.messages.admin.reload);
    }
}
