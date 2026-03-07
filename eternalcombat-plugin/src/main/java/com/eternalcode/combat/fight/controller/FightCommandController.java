package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FightCommandController implements Listener {

    private static final char SLASH = '/';
    private static final char NAMESPACE_SEPARATOR = ':';
    private static final char SPACE = ' ';
    private static final String EMPTY = "";
    private static final String SINGLE_SPACE = " ";
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;

    public FightCommandController(FightManager fightManager, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
    }

    static String normalizeCommand(String command) {
        if (command == null || command.isBlank()) {
            return EMPTY;
        }

        String normalized = command.strip();

        if (normalized.charAt(0) == SLASH) {
            normalized = normalized.substring(1).stripLeading();
        }

        if (normalized.isEmpty()) {
            return EMPTY;
        }

        int colonIndex = normalized.indexOf(NAMESPACE_SEPARATOR);
        int firstSpaceIndex = normalized.indexOf(SPACE);

        if (colonIndex >= 0 && (firstSpaceIndex < 0 || colonIndex < firstSpaceIndex)) {
            normalized = normalized.substring(colonIndex + 1);
        }

        return WHITESPACE_PATTERN.matcher(normalized).replaceAll(SINGLE_SPACE);
    }

    static boolean matches(String command, List<String> restrictedCommands) {
        if (command.isEmpty()) {
            return false;
        }

        for (String restrictedCommand : restrictedCommands) {
            String normalizedRestricted = normalizeCommand(restrictedCommand);

            if (normalizedRestricted.isEmpty()) {
                continue;
            }

            boolean exactMatch = command.equalsIgnoreCase(normalizedRestricted);
            boolean prefixMatch = command.regionMatches(true, 0, normalizedRestricted, 0, normalizedRestricted.length())
                && command.length() > normalizedRestricted.length()
                && command.charAt(normalizedRestricted.length()) == SPACE;

            if (exactMatch || prefixMatch) {
                return true;
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        String command = normalizeCommand(event.getMessage());

        if (command.isEmpty()) {
            return;
        }

        boolean matched = matches(command, this.config.commands.restrictedCommands);

        WhitelistBlacklistMode mode = this.config.commands.commandRestrictionMode;

        if (!mode.shouldBlock(matched)) {
            return;
        }

        event.setCancelled(true);
        this.noticeService.create()
            .player(uniqueId)
            .notice(this.config.messagesSettings.commandDisabledDuringCombat)
            .send();
    }
}
