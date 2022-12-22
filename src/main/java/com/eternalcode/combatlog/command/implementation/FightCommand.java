package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;

import java.util.UUID;

@Route(name = "combatlog")
public class FightCommand {

    private final CombatManager combatManager;
    private final NotificationAnnouncer announcer;
    private final MessageConfig messages;

    public FightCommand(CombatManager combatManager, NotificationAnnouncer announcer, MessageConfig messages) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.messages = messages;
    }

    @Execute(route = "fight")
    @Permission("eternalcombatlog.fight")
    void execute(Player player) {
        UUID uniqueId = player.getUniqueId();

        this.announcer.sendMessage(player, this.combatManager.isInCombat(uniqueId)
                ? this.messages.inCombat
                : this.messages.notInCombat);
    }
}
