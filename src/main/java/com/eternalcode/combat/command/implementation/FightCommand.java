package com.eternalcode.combat.command.implementation;

import com.eternalcode.combat.NotificationAnnouncer;
import com.eternalcode.combat.combat.CombatManager;
import com.eternalcode.combat.config.implementation.MessageConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.util.UUID;

@Route(name = "fight")
@Permission("combatlog.fight")
public class FightCommand {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final MessageConfig messages;

    public FightCommand(CombatManager combatManager, NotificationAnnouncer announcer, MessageConfig messages) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.messages = messages;
    }

    @Execute(route = "fight", min = 1)
    void execute(Player player, @Arg @Name("target") Player target) {
        UUID targetUniqueId = target.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        this.announcer.announceMessage(playerUniqueId, this.combatManager.isInCombat(targetUniqueId)
                ? this.messages.inCombat
                : this.messages.notInCombat);
    }
}
