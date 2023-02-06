package com.eternalcode.combat.command.implementation;

import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.PluginConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.UUID;

@Route(name = "combatlog fight")
@Permission("eternalcombat.fight")
public class FightCommand {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;
    private final Server server;

    public FightCommand(CombatManager combatManager, NotificationAnnouncer announcer, PluginConfig config, Server server) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.config = config;
        this.server = server;
    }

    @Execute(route = "status", required = 1)
    @Permission("eternalcombat.fight.status")
    void status(Player player, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        PluginConfig.Messages messages = this.config.messages;

        this.announcer.sendMessage(player, this.combatManager.isInCombat(targetUniqueId)
            ? messages.inCombat
            : messages.notInCombat);
    }

    @Execute(route = "tag", required = 1)
    @Permission("eternalcombat.fight.tag")
    void tag(Player player, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        Duration time = this.config.settings.combatLogTime;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.combatManager.tag(targetUniqueId, time);
        this.announcer.sendMessage(player, formatter.format(this.config.messages.adminTagPlayer));
    }

    @Execute(route = "tag", required = 2)
    @Permission("eternalcombat.fight.tag")
    public void tagMultiple(Player player, @Arg Player firstTarget, @Arg Player secondTarget) {
        Duration combatTime = this.config.settings.combatLogTime;
        PluginConfig.Messages messages = this.config.messages;

        UUID firstTargetUniqueId = firstTarget.getUniqueId();
        UUID secondTargetUniqueId = secondTarget.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        if (playerUniqueId.equals(firstTargetUniqueId) || playerUniqueId.equals(secondTargetUniqueId)) {
            this.announcer.sendMessage(player, messages.cantTagSelf);
            return;
        }

        this.combatManager.tag(firstTargetUniqueId, combatTime);
        this.combatManager.tag(secondTargetUniqueId, combatTime);

        Formatter formatter = new Formatter()
            .register("{FIRST_PLAYER}", firstTarget.getName())
            .register("{SECOND_PLAYER}", secondTarget.getName());

        String format = formatter.format(messages.adminTagPlayerMultiple);
        this.announcer.sendMessage(player, format);

        this.announcer.sendMessage(firstTarget, messages.tagPlayer);
        this.announcer.sendMessage(secondTarget, messages.tagPlayer);
    }

    @Execute(route = "untag", required = 1)
    @Permission("eternalcombat.fight.untag")
    void untag(Player player, @Arg Player target) {
        UUID uniqueId = target.getUniqueId();
        UUID enemyUuid = this.combatManager.getEnemy(uniqueId);

        Player enemy = server.getPlayer(enemyUuid);

        if (enemy == null) {
            return;
        }

        UUID enemyUniqueId = enemy.getUniqueId();

        this.announcer.sendMessage(target, this.config.messages.unTagPlayer);
        this.announcer.sendMessage(enemy, this.config.messages.unTagPlayer);

        this.combatManager.remove(uniqueId);
        this.combatManager.remove(enemyUniqueId);

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.announcer.sendMessage(player, formatter.format(this.config.messages.adminUnTagPlayer));
    }
}
