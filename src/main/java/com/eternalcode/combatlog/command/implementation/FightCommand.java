package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.entity.Player;

import java.util.UUID;

@Section(route = "fight", aliases = "walka")
public class FightCommand {

    private final CombatManager combatManager;
    private final MessageAnnouncer announcer;
    private final MessageConfig messages;

    public FightCommand(CombatManager combatManager, MessageAnnouncer announcer, MessageConfig messages) {
        this.combatManager = combatManager;
        this.announcer = announcer;
        this.messages = messages;
    }

    @Execute
    void excute(Player player) {
        UUID uniqueId = player.getUniqueId();

        if (this.combatManager.isInCombat(uniqueId)) {
            this.announcer.sendMessage(uniqueId, this.messages.inCombat);
        }

        else {
            this.announcer.sendMessage(uniqueId, this.messages.notInCombat);
        }
    }
}
