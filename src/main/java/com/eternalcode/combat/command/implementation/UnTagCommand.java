package com.eternalcode.combat.command.implementation;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.MessageConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

@Route(name = "untag")
@Permission("eternalcombat.untag")
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
    @Permission("eternalcombat.untag")
    public void execute(Player player, @Arg @Name("target") Player target) {
        UUID uniqueId = target.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();
        UUID enemyUuid = this.combatManager.getEnemy(uniqueId);

        Player enemy = server.getPlayer(enemyUuid);

        if (enemy == null) {
            return;
        }

        UUID enemyUniqueId = enemy.getUniqueId();

        this.notificationAnnouncer.announceMessage(uniqueId, this.messageConfig.unTagPlayer);
        this.notificationAnnouncer.announceMessage(enemyUniqueId, this.messageConfig.unTagPlayer);

        this.combatManager.remove(uniqueId);
        this.combatManager.remove(enemyUniqueId);

        Formatter formatter = new Formatter()
                .register("{PLAYER}", target.getName());

        this.notificationAnnouncer.announceMessage(playerUniqueId, formatter.format(this.messageConfig.adminUnTagPlayer));
    }
}
