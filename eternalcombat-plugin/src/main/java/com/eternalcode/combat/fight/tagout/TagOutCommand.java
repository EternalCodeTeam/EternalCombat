package com.eternalcode.combat.fight.tagout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Route(name = "tagout", aliases = "tagimmunity")
public class TagOutCommand {

    private final FightTagOutService fightTagOutService;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public TagOutCommand(FightTagOutService fightTagOutService, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightTagOutService = fightTagOutService;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute(required = 1)
    @Permission("eternalcombat.tagout")
    void tagout(Player sender, @Arg Duration time) {
        UUID targetUniqueId = sender.getUniqueId();

        Formatter formatter = new Formatter()
            .register("{PLAYER}", sender.getName())
            .register("{TIME}", DurationUtil.format(time));

        this.fightTagOutService.tagOut(targetUniqueId, time);

        String format = formatter.format(this.config.messages.admin.adminTagOutSelf);
        this.announcer.sendMessage(sender, format);
    }

    @Execute(required = 2)
    @Permission("eternalcombat.tagout")
    void tagout(Player sender, @Arg Player target, @Arg Duration time) {
        UUID targetUniqueId = target.getUniqueId();

        Instant now = Instant.now();
        Duration remaining = Duration.between(now, now.plus(time));

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName())
            .register("{TIME}", DurationUtil.format(remaining));

        this.fightTagOutService.tagOut(targetUniqueId, time);

        String adminTagOutFormat = formatter.format(this.config.messages.admin.adminTagOut);
        this.announcer.sendMessage(sender, adminTagOutFormat);

        String playerTagOutFormat = formatter.format(this.config.messages.admin.playerTagOut);
        this.announcer.sendMessage(target, playerTagOutFormat);
    }

    @Execute(route = "remove", required = 1)
    @Permission("eternalcombat.tagout")
    void untagout(Player sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();

        this.fightTagOutService.unTagOut(targetUniqueId);

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        if (!targetUniqueId.equals(sender.getUniqueId())) {
            String adminUnTagOutFormat = formatter.format(this.config.messages.admin.adminTagOutOff);
            this.announcer.sendMessage(sender, adminUnTagOutFormat);
        }

        this.announcer.sendMessage(target, this.config.messages.admin.playerTagOutOff);
    }

    @Execute(route = "remove", required = 0)
    @Permission("eternalcombat.tagout")
    void untagout(Player sender) {
        UUID senderUniqueId = sender.getUniqueId();

        this.fightTagOutService.unTagOut(senderUniqueId);

        this.announcer.sendMessage(sender, this.config.messages.admin.playerTagOutOff);
    }
}

