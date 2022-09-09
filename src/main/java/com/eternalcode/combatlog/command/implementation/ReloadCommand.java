package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.config.ConfigManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.entity.Player;

import java.util.UUID;

@Section(route = "combatrl", aliases = "combatreload")
@Permission("eternalcombatlog.reload")
public class ReloadCommand {

    private final ConfigManager configManager;
    private final MessageAnnouncer announcer;
    private final MessageConfig messages;

    public ReloadCommand(ConfigManager configManager, MessageAnnouncer announcer, MessageConfig messages) {
        this.configManager = configManager;
        this.announcer = announcer;
        this.messages = messages;
    }

    @Execute
    void execute(Player player) {
        UUID uniqueId = player.getUniqueId();

        this.configManager.reload();

        this.announcer.sendMessage(uniqueId, this.messages.reload);
    }
}
