package com.eternalcode.combat.config.implementation;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class MessagesSettings extends OkaeriConfig {

    @Comment({
        "# Customize messages related to admin commands and notifications.",
        "# These messages are displayed to server administrators."
    })
    public AdminMessages admin = new AdminMessages();

    @Comment({
        " ",
        "# Configure the combat log notification displayed to players.",
        "# You can use the {TIME} variable to display the remaining combat time.",
        " ",
        "# BossBar progress: Set to -1.0 to show the remaining combat time as a progress bar.",
        "# BossBar colors: https://javadoc.io/static/net.kyori/adventure-api/4.14.0/net/kyori/adventure/bossbar/BossBar.Color.html",
        "# BossBar overlays: https://javadoc.io/static/net.kyori/adventure-api/4.14.0/net/kyori/adventure/bossbar/BossBar.Overlay.html"
    })
    public Notice combatNotification = BukkitNotice.builder()
        .actionBar("<bold>Combat ends in: <red>{TIME}</red></bold>")
        .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 2.0F, 1.0F)
        .build();

    @Comment({
        "# Would you like to display milliseconds instead of seconds in combat notification "
    })
    public boolean withoutMillis = true;

    @Comment({
        "# Message displayed when a player lacks permission to execute a command.",
        "# The {PERMISSION} placeholder is replaced with the required permission."
    })
    public Notice noPermission = Notice.chat(
        "<gradient:red:dark_red>You don't have permission <dark_gray>(<gray>{PERMISSION}</gray>)</dark_gray> to perform this command!</gradient>");

    @Comment({
        "# Message displayed when a specified player is not found.",
        "# This message is shown when a command targets a player who is not online or does not exist."
    })
    public Notice playerNotFound =
        Notice.chat("<gradient:#ff0000:#ff6b6b>The specified player could not be found!</gradient>");

    @Comment({
        "# Message displayed when a player enters combat.",
        "# This message warns the player not to leave the server while in combat."
    })
    public Notice playerTagged = Notice.chat(
        "<gradient:red:yellow>⚠ <white>You are in combat!</white> <u>Do not leave the server!</gradient>");

    @Comment({
        "# Message displayed when a player leaves combat.",
        "# This message informs the player that they can safely leave the server."
    })
    public Notice playerUntagged = Notice.chat(
        "<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</gradient>");

    @Comment({
        "# Broadcast message displayed when a player logs out during combat.",
        "# The {PLAYER} placeholder is replaced with the player's name."
    })
    public Notice playerLoggedOutDuringCombat =
        Notice.chat("<gradient:red:dark_red>⚠ <white>{PLAYER}</white> logged off during combat!</gradient>");

    @Comment({
        "# Message displayed when a player attempts to use a disabled command during combat.",
        "# This message informs the player that the command is prohibited while in combat."
    })
    public Notice commandDisabledDuringCombat = Notice.chat(
        "<gradient:red:yellow>⚠ <white>Command blocked!</white> Cannot use this during combat!</gradient>");

    @Comment({
        "# Message displayed when a player uses a command with incorrect arguments.",
        "# The {USAGE} placeholder is replaced with the correct command syntax."
    })
    public Notice invalidCommandUsage = Notice.chat("<gray>Usage: <gradient:gold:yellow>{USAGE}</gradient>");

    @Comment({
        "# Message displayed when a player attempts to open their inventory during combat.",
        "# This message informs the player that inventory access is blocked while in combat."
    })
    public Notice inventoryBlockedDuringCombat = Notice.chat(
        "<gradient:red:dark_red>⚠ <white>Inventory access</white> is restricted during combat!</gradient>");

    @Comment({
        "# Message displayed when a player attempts to place a block during combat.",
        "# The {MODE} placeholder is replaced with the block placement mode (ABOVE/BELOW).",
        "# The {Y} placeholder is replaced with the Y coordinate set in the config."
    })
    public Notice blockPlacingBlockedDuringCombat =
        Notice.chat("<gradient:red:yellow>⚠ Block placement <white>{MODE} Y:{Y}</white> is restricted!</gradient>");

    @Comment({
        "# Message displayed when a player attempts to enter a restricted region during combat.",
        "# This message informs the player that they cannot enter the region while in combat."
    })
    public Notice cantEnterOnRegion = Notice.chat(
        "<gradient:red:dark_red>⚠ Restricted area!</gradient> <white>Cannot enter during combat!</white>");

    public static class AdminMessages extends OkaeriConfig {
        @Comment({
            "# Message displayed when the console attempts to use a player-only command.",
            "# This message informs the console that the command is not available for non-players."
        })
        public Notice onlyForPlayers =
            Notice.chat("<gradient:red:dark_red>❌ This command is player-only!</gradient>");

        @Comment({
            "# Message displayed to an admin when they tag a player.",
            "# The {PLAYER} placeholder is replaced with the tagged player's name."
        })
        public Notice adminTagPlayer =
            Notice.chat("<gradient:#00b3ff:#0066ff>⚔ <gray>Tagged player:</gray> <white>{PLAYER}</white></gradient>");

        @Comment({
            "# Message displayed when an admin tags multiple players.",
            "# The {FIRST_PLAYER} and {SECOND_PLAYER} placeholders are replaced with the players' names."
        })
        public Notice adminTagMultiplePlayers = Notice.chat(
            "<gradient:#00b3ff:#0066ff>⚔ <gray>Tagged:</gray> <white>{FIRST_PLAYER}</white> <gray>and</gray> <white>{SECOND_PLAYER}</white></gradient>");

        @Comment({
            "# Message displayed to an admin when they remove a player from combat.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice adminUntagPlayer = Notice.chat(
            "<gradient:#00ff88:#00b300>✌ <gray>Removed</gray> <white>{PLAYER}</white> <gray>from combat</gray></gradient>");

        @Comment({
            "# Message displayed to an admin when they remove a player from combat.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice adminUntagAll = Notice.chat(
            "<gradient:#00ff88:#00b300>✌ <gray>Removed</gray> <white>{COUNT}</white> <gray> players from combat</gray></gradient>");

        @Comment({
            "# Message displayed when an admin attempts to untag a player who is not in combat.",
            "# This message informs the admin that the player is not currently in combat."
        })
        public Notice adminPlayerNotInCombat =
            Notice.chat("<gradient:red:dark_red>❌ <white>{PLAYER}</white> is not in combat!</gradient>");

        @Comment({
            "# Message displayed when a player is in combat.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice playerInCombat =
            Notice.chat("<gradient:#ff6666:#ff0000>⚔ <white>{PLAYER}</white> <gray>is in combat!</gray></gradient>");

        @Comment({
            "# Message displayed when a player is not in combat.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice playerNotInCombat =
            Notice.chat("<gradient:#00ff00:#00b300>✌ <white>{PLAYER}</white> <gray>is safe</gray></gradient>");

        @Comment({
            "# Message displayed when an admin attempts to tag themselves.",
            "# This message informs the admin that they cannot tag themselves."
        })
        public Notice adminCannotTagSelf = Notice.chat("<gradient:red:dark_red>❌ Cannot tag yourself!</gradient>");

        @Comment({
            "# Message displayed when an admin disables combat tagging for themselves.",
            "# The {TIME} placeholder is replaced with the remaining fight time."
        })
        public Notice adminTagOutSelf = Notice.chat(
            "<gradient:#00b3ff:#0066ff>🛡 <gray>Self-protection active for</gray> <white>{TIME}</white></gradient>");

        @Comment({
            "# Message displayed when an admin disables combat tagging for another player.",
            "# The {PLAYER} placeholder is replaced with the player's name.",
            "# The {TIME} placeholder is replaced with the remaining fight time."
        })
        public Notice adminTagOut = Notice.chat(
            "<gradient:#00b3ff:#0066ff>🛡 <gray>Protected</gray> <white>{PLAYER}</white> <gray>for</gray> <white>{TIME}</white></gradient>");

        @Comment({
            "# Message displayed to a player when their combat tagging is disabled.",
            "# The {TIME} placeholder is replaced with the remaining fight time."
        })
        public Notice playerTagOut = Notice.chat(
            "<gradient:#00ff88:#00b300>🛡 <gray>Protection active for</gray> <white>{TIME}</white></gradient>");

        @Comment({
            "# Message displayed when an admin reenables combat tagging for a player.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice adminTagOutOff = Notice.chat(
            "<gradient:#00ff88:#00b300>✌ <gray>Re-enabled tagging for</gray> <white>{PLAYER}</white></gradient>");

        @Comment({
            "# Message displayed to a player when their combat tagging is reenabled.",
            "# This message informs the player that they can now be tagged again."
        })
        public Notice playerTagOutOff = Notice.chat("");

        @Comment({
            "# Message displayed when an admin attempts to tag a player who has tag-out enabled.",
            "# This message informs the admin that the player cannot be tagged at this time."
        })
        public Notice adminTagOutCanceled =
            Notice.chat("<gradient:red:dark_red>❌ Player has tag-out protection!</gradient>");
    }
}
