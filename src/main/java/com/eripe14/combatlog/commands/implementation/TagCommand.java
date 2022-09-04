package com.eripe14.combatlog.commands.implementation;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.config.PluginConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

@Section(route = "tag")
@Permission("eternalcombatlog.tag")
public class TagCommand {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;

    public TagCommand(CombatLogManager combatLogManager, MessageConfig messageConfig, PluginConfig pluginConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
    }

    @Execute(min = 2)
    public void execute(CommandSender commandSender, @Arg @Name("firstTarget") Player firstTarget, @Arg @Name("secondTarget") Player secondTarget) {
        this.combatLogManager.tag(firstTarget.getUniqueId(), secondTarget.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));
        this.combatLogManager.tag(secondTarget.getUniqueId(), firstTarget.getUniqueId(), Duration.ofSeconds(this.pluginConfig.combatLogTime));

        commandSender.sendMessage(ChatUtil.fixColor(this.messageConfig.adminTagPlayer
                .replaceAll("%FIRST_PLAYER%", firstTarget.getName())
                .replaceAll("%SECOND_PLAYER%", secondTarget.getName())));

        firstTarget.sendMessage(ChatUtil.fixColor(this.messageConfig.tagPlayer));
        secondTarget.sendMessage(ChatUtil.fixColor(this.messageConfig.tagPlayer));
    }

}
