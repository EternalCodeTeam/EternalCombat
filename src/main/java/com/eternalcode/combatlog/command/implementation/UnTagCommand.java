package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

@Route(name = "combatlog")
public class UnTagCommand {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public UnTagCommand(CombatManager combatManager, MessageConfig messageConfig, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Execute(route = "untag", min = 1)
    @Permission("eternalcombatlog.untag")
    public void execute(Player player, @Arg @Name("target") Player target) {
        UUID enemyUuid = this.combatManager.getEnemy(target.getUniqueId());

        Player enemy = server.getPlayer(enemyUuid);

        if (enemy == null) {
            return;
        }

        this.notificationAnnouncer.sendMessage(target, this.messageConfig.unTagPlayer);
        this.notificationAnnouncer.sendMessage(enemy, this.messageConfig.unTagPlayer);

        this.combatManager.remove(target.getUniqueId());
        this.combatManager.remove(enemy.getUniqueId());

        Formatter formatter = new Formatter()
                .register("{PLAYER}", target.getName());

        this.notificationAnnouncer.sendMessage(player, formatter.format(this.messageConfig.adminUnTagPlayer));
    }
}
