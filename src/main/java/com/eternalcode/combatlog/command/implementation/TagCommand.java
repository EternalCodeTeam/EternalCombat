package com.eternalcode.combatlog.command.implementation;

import com.eternalcode.combatlog.NotificationAnnouncer;
import com.eternalcode.combatlog.combat.CombatManager;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.config.implementation.PluginConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.UUID;

@Route(name = "tag")
@Permission("eternalcombat.tag")
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
    public void execute(Player player, @Arg @Name("firstTarget") Player firstTarget, @Arg @Name("secondTarget") Player secondTarget) {
        Duration combatTime = this.pluginConfig.combatLogTime;

        UUID firstTargetUniqueId = firstTarget.getUniqueId();
        UUID secondTargetUniqueId = secondTarget.getUniqueId();
        UUID playerUniqueId = player.getUniqueId();

        if (playerUniqueId.equals(firstTargetUniqueId) || playerUniqueId.equals(secondTargetUniqueId)) {
            this.notificationAnnouncer.announceMessage(playerUniqueId, this.messageConfig.cantTagSelf);
            return;
        }

        this.combatManager.tag(firstTargetUniqueId, secondTargetUniqueId, combatTime);
        this.combatManager.tag(secondTargetUniqueId, firstTargetUniqueId, combatTime);

        Formatter formatter = new Formatter()
                .register("{FIRST_PLAYER}", firstTarget.getName())
                .register("{SECOND_PLAYER}", secondTarget.getName());

        String format = formatter.format(this.messageConfig.adminTagPlayer);
        this.notificationAnnouncer.announceMessage(playerUniqueId, format);

        this.notificationAnnouncer.announceMessage(firstTargetUniqueId, this.messageConfig.tagPlayer);
        this.notificationAnnouncer.announceMessage(secondTargetUniqueId, this.messageConfig.tagPlayer);
    }

}
