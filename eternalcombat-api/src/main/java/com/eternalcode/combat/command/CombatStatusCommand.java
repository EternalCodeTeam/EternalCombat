package com.eternalcode.combat.command;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

@Command(name = "combatlog", aliases = "combat")
public class CombatStatusCommand {

    private final FightManager fightManager;
    private final NotificationAnnouncer announcer;
    private final PluginConfig config;

    public CombatStatusCommand(FightManager fightManager, NotificationAnnouncer announcer, PluginConfig config) {
        this.fightManager = fightManager;
        this.announcer = announcer;
        this.config = config;
    }

    @Execute(name = "status")
    @Permission("eternalcombat.status")
    void status(@Context CommandSender sender, @Arg Player target) {
        UUID targetUniqueId = target.getUniqueId();
        PluginConfig.Messages messages = this.config.messages;

        Formatter formatter = new Formatter()
            .register("{PLAYER}", target.getName());

        this.announcer.sendMessage(sender, this.fightManager.isInCombat(targetUniqueId)
            ? formatter.format(messages.admin.playerInCombat)
            : formatter.format(messages.admin.playerNotInCombat));
    }
}
