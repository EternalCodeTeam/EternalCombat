package com.eripe14.combatlog.commands.implementation;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import dev.rollczi.litecommands.argument.Arg;
import dev.rollczi.litecommands.argument.Name;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Section(route = "untag")
@Permission("eternalcombatlog.untag")
public class UnTagCommand {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;

    public UnTagCommand(CombatLogManager combatLogManager, MessageConfig messageConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
    }

    @Execute(min = 1)
    public void execute(CommandSender commandSender, @Arg @Name("target") Player target) {
        UUID enemyUuid = this.combatLogManager.getEnemy(target.getUniqueId());

        Player enemy = Bukkit.getPlayer(enemyUuid);

        if (enemy == null) return;

        target.sendMessage(ChatUtil.fixColor(this.messageConfig.unTagPlayer));
        enemy.sendMessage(ChatUtil.fixColor(this.messageConfig.unTagPlayer));

        this.combatLogManager.remove(target.getUniqueId());
        this.combatLogManager.remove(enemy.getUniqueId());

        commandSender.sendMessage(ChatUtil.fixColor(this.messageConfig.adminUnTagPlayer
                .replaceAll("%PLAYER%", target.getName())));
    }
}
