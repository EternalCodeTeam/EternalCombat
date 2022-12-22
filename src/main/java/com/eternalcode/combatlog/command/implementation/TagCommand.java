package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import com.eternalcode.combatlog.NotificationAnnouncer;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;

@Route(name = "combatlog")
public class TagCommand {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final PluginConfig pluginConfig;
    private final NotificationAnnouncer notificationAnnouncer;

    public TagCommand(CombatManager combatManager, MessageConfig messageConfig, PluginConfig pluginConfig, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.pluginConfig = pluginConfig;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Execute(route = "tag", min = 2)
    @Permission("eternalcombatlog.tag")
    public void execute(Player player, @Arg @Name("firstTarget") Player firstTarget, @Arg @Name("secondTarget") Player secondTarget) {
        Duration combatTime = this.pluginConfig.combatLogTime;

        this.combatManager.tag(firstTarget.getUniqueId(), secondTarget.getUniqueId(), combatTime);
        this.combatManager.tag(secondTarget.getUniqueId(), firstTarget.getUniqueId(), combatTime);

        Formatter formatter = new Formatter()
                .register("{FIRST_PLAYER}", firstTarget.getName())
                .register("{SECOND_PLAYER}", secondTarget.getName());

        String format = formatter.format(this.messageConfig.adminTagPlayer);
        this.notificationAnnouncer.sendMessage(player, format);

        this.notificationAnnouncer.sendMessage(firstTarget, this.messageConfig.tagPlayer);
        this.notificationAnnouncer.sendMessage(secondTarget, this.messageConfig.tagPlayer);
    }

}
