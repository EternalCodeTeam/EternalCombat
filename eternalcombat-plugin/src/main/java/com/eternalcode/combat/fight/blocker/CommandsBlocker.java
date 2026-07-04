package com.eternalcode.combat.fight.blocker;

import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.StringUtil;

public class CommandsBlocker implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;

    public CommandsBlocker(FightManager fightManager, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
    }

    @EventHandler
    void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerUniqueId)) {
            return;
        }

        String command = normalizeCommand(event.getMessage().substring(1));

        boolean isAnyMatch = this.config.commands.restrictedCommands.stream()
            .anyMatch(restrictedCommand -> StringUtil.startsWithIgnoreCase(command, restrictedCommand));

        WhitelistBlacklistMode mode = this.config.commands.commandRestrictionMode;

        boolean shouldCancel = mode.shouldBlock(isAnyMatch);

        if (shouldCancel) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerUniqueId)
                .notice(this.config.messagesSettings.commandDisabledDuringCombat)
                .send();

        }
    }

    // Trim leading spaces and strip any namespace prefix (e.g. "minecraft:tp" -> "tp")
    // from the command label so blacklisted commands cannot be bypassed with those forms.
    private static String normalizeCommand(String rawCommand) {
        String command = rawCommand.stripLeading();

        int spaceIndex = command.indexOf(' ');
        String label = spaceIndex == -1 ? command : command.substring(0, spaceIndex);

        int colonIndex = label.indexOf(':');
        if (colonIndex == -1) {
            return command;
        }

        String rest = spaceIndex == -1 ? "" : command.substring(spaceIndex);
        return label.substring(colonIndex + 1) + rest;
    }

}
