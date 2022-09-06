package com.eripe14.combatlog.command.implementation;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

@Section(route = "untag")
@Permission("eternalcombatlog.untag")
public class UnTagCommand {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public UnTagCommand(CombatLogManager combatLogManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
    }

    @Execute(min = 1)
    public void execute(Player player, @Arg @Name("target") Player target) {
        UUID enemyUuid = this.combatLogManager.getEnemy(target.getUniqueId());

        Player enemy = server.getPlayer(enemyUuid);

        if (enemy == null) {
            return;
        }

        this.messageAnnouncer.sendMessage(target.getUniqueId(), this.messageConfig.unTagPlayer);
        this.messageAnnouncer.sendMessage(enemy.getUniqueId(), this.messageConfig.unTagPlayer);

        this.combatLogManager.remove(target.getUniqueId());
        this.combatLogManager.remove(enemy.getUniqueId());

        Formatter formatter = new Formatter();

        formatter.register("{PLAYER}", target.getName());

        this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.adminUnTagPlayer);
    }
}
