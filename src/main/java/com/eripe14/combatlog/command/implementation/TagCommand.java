package com.eripe14.combatlog.command.implementation;

import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;

@Section(route = "tag")
@Permission("eternalcombatlog.tag")
public class TagCommand {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;
    private final MessageAnnouncer messageAnnouncer;

    public TagCommand(CombatLogManager combatLogManager, MessageConfig messageConfig, PluginConfig pluginConfig, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
        this.messageAnnouncer = messageAnnouncer;
    }

    @Execute(min = 2)
    public void execute(Player player, @Arg @Name("firstTarget") Player firstTarget, @Arg @Name("secondTarget") Player secondTarget) {
        this.combatLogManager.tag(firstTarget.getUniqueId(), secondTarget.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));
        this.combatLogManager.tag(secondTarget.getUniqueId(), firstTarget.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));

        Formatter formatter = new Formatter();

        formatter.register("{FIRST_PLAYER}", firstTarget.getName());
        formatter.register("{SECOND_PLAYER}", secondTarget.getName());

        this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.adminTagPlayer);

        this.messageAnnouncer.sendMessage(firstTarget.getUniqueId(), this.messageConfig.tagPlayer);
        this.messageAnnouncer.sendMessage(secondTarget.getUniqueId(), this.messageConfig.tagPlayer);
    }

}
