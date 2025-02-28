package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.border.BorderSettings;
import com.eternalcode.combat.fight.drop.DropSettings;
import com.eternalcode.combat.fight.effect.FightEffectSettings;
import com.eternalcode.combat.fight.knockback.KnockbackSettings;
import com.eternalcode.combat.fight.pearl.FightPearlSettings;
import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class PluginConfig extends OkaeriConfig {

    @Comment(" ")
    @Comment("#  _____ _     EternalCombat      _ _____                 _           _    ")
    @Comment("# |  ___| |  o()xxx[{:::::::::>  | /  __ \\               | |         | |   ")
    @Comment("# | |__ | |_ ___ _ __ _ __   __ _| | /  \\/ ___  _ __ ___ | |__   __ _| |_  ")
    @Comment("# |  __|| __/ _ \\ '__| '_ \\ / _` | | |    / _ \\| '_ ` _ \\| '_ \\ / _` | __| ")
    @Comment("# | |___| ||  __/ |  | | | | (_| | | \\__/\\ (_) | | | | | | |_) | (_| | |_  ")
    @Comment("# \\____/ \\__\\___|_|  |_| |_|\\__,_|_|\\____/\\___/|_| |_| |_|_.__/ \\__,_|\\__| ")
    @Comment(" ")

    @Comment({
        "# Settings for the plugin.",
        "# Modify these to customize the plugin's behavior."
    })
    public Settings settings = new Settings();

    @Comment({
        " ",
        "# Settings related to Ender Pearls.",
        "# Configure cooldowns, restrictions, and other behaviors for Ender Pearls during combat."
    })
    public FightPearlSettings pearl = new FightPearlSettings();

    @Comment({
        " ",
        "# Custom effects applied during combat.",
        "# Configure effects like blindness, slowness, or other debuffs that are applied to players in combat."
    })
    public FightEffectSettings effect = new FightEffectSettings();

    @Comment({
        " ",
        "# Customize how items are dropped when a player dies during combat.",
        "# Configure whether items drop, how they drop, and any additional rules for item drops."
    })
    public DropSettings drop = new DropSettings();

    public KnockbackSettings knockback = new KnockbackSettings();

    @Comment({
        "",
        "# Border Settings",
    })
    public BorderSettings border = new BorderSettings();

    public static class Settings extends OkaeriConfig {

        @Comment({
            "# Notify players about new plugin updates when they join the server.",
            "# Set to 'true' to enable update notifications, or 'false' to disable them."
        })
        public boolean notifyAboutUpdates = true;

        @Comment({
            "# The duration (in seconds) that a player remains in combat after being attacked.",
            "# After this time expires, the player will no longer be considered in combat."
        })
        public Duration combatTimerDuration = Duration.ofSeconds(20);

        @Comment({
            "# List of worlds where combat logging is disabled.",
            "# Players in these worlds will not be tagged or affected by combat rules."
        })
        public List<String> ignoredWorlds = List.of(
            "your_world"
        );

        @Comment({
            "# List of regions where combat is restricted.",
            "# Players in these regions will not be able to engage in combat."
        })
        public List<String> blockedRegions = Collections.singletonList("your_region");

        @Comment({
            "# Prevent players from entering regions where PVP is disabled by WorldGuard.",
            "# Set to 'true' to enforce this restriction, or 'false' to allow PVP in all regions."
        })
        public boolean preventPvpInRegions = true;

        @Comment({
            "# Define the radius of restricted regions if WorldGuard is not used.",
            "# This setting is based on the default spawn region."
        })
        public int restrictedRegionRadius = 10;

        @Comment({
            "# Automatically release the attacker from combat when the victim dies.",
            "# Set to 'true' to enable this feature, or 'false' to keep the attacker in combat."
        })
        public boolean releaseAttackerOnVictimDeath = true;

        @Comment({
            "# Exclude server administrators from combat tagging and being tagged.",
            "# Set to 'true' to prevent admins from being tagged or tagging others.",
            "# Set to 'false' to allow admins to participate in combat."
        })
        public boolean excludeAdminsFromCombat = false;

        @Comment({
            "# Exclude players in creative mode from combat tagging and being tagged.",
            "# Set to 'true' to prevent creative mode players from being tagged or tagging others.",
            "# Set to 'false' to allow creative mode players to participate in combat."
        })
        public boolean excludeCreativePlayersFromCombat = false;

        @Comment({
            "# Set the mode for command restrictions during combat.",
            "# Available modes: WHITELIST (only listed commands are allowed), BLACKLIST (listed commands are blocked)."
        })
        public WhitelistBlacklistMode commandRestrictionMode = WhitelistBlacklistMode.BLACKLIST;

        @Comment({
            "# List of commands affected by the command restriction mode.",
            "# In BLACKLIST mode, these commands are blocked. In WHITELIST mode, only these commands are allowed."
        })
        public List<String> restrictedCommands = List.of(
            "gamemode",
            "spawn",
            "tp",
            "tpa",
            "tpaccept"
        );

        @Comment({
            "# Disable the use of elytra during combat.",
            "# Set to 'true' to prevent players from using elytra while in combat."
        })
        public boolean disableElytraUsage = true;

        @Comment({
            "# Disable the use of elytra when a player takes damage.",
            "# Set to 'true' to disable elytra usage upon taking damage, even when the player is mid-air."
        })
        public boolean disableElytraOnDamage = true;

        @Comment({
            "# Prevent players from flying during combat.",
            "# Flying players will fall to the ground if this is enabled."
        })
        public boolean disableFlying = true;

        @Comment({
            "# Prevent players from opening their inventory during combat.",
            "# Set to 'true' to block inventory access while in combat."
        })
        public boolean disableInventoryAccess = false;

        @Comment({
            "# Prevent players from placing blocks during combat.",
            "# Set to 'true' to block block placement while in combat."
        })
        public boolean disableBlockPlacing = true;

        @Comment({
            "# Restrict block placement above or below a specific Y coordinate.",
            "# Available modes: ABOVE (blocks cannot be placed above the Y coordinate), BELOW (blocks cannot be placed below the Y coordinate)."
        })
        public BlockPlacingMode blockPlacementMode = BlockPlacingMode.ABOVE;

        @Comment({
            "# Custom name for the block placement mode used in messages.",
            "# This name will be displayed in notifications related to block placement restrictions."
        })
        public String blockPlacementModeDisplayName = "above";

        @Comment({
            "# Define the Y coordinate for block placement restrictions.",
            "# This value is relative to the selected block placement mode (ABOVE or BELOW)."
        })
        public int blockPlacementYCoordinate = 40;

        public enum BlockPlacingMode {
            ABOVE,
            BELOW
        }

        @Comment({
            "# Restrict the placement of specific blocks during combat.",
            "# Add blocks to this list to prevent their placement. Leave the list empty to block all blocks.",
            "# Note: This feature requires 'disableBlockPlacing' to be enabled."
        })
        public List<Material> restrictedBlockTypes = List.of();

        @Comment({
            "# Enable or disable combat logging for damage caused by non-player entities.",
            "# Set to 'true' to log damage from non-player sources, or 'false' to disable this feature."
        })
        public boolean enableDamageCauseLogging = false;

        @Comment({
            "# Set the mode for logging damage causes.",
            "# Available modes: WHITELIST (only listed causes are logged), BLACKLIST (all causes except listed ones are logged)."
        })
        public WhitelistBlacklistMode damageCauseRestrictionMode = WhitelistBlacklistMode.WHITELIST;

        @Comment({
            "# List of damage causes to be logged based on the selected mode.",
            "# In WHITELIST mode, only these causes are logged. In BLACKLIST mode, all causes except these are logged.",
            "# For a full list of damage causes, visit: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html"
        })
        public List<EntityDamageEvent.DamageCause> loggedDamageCauses = List.of(
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.CONTACT,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK
        );

        @Comment({
            "# List of projectile types that do not trigger combat tagging.",
            "# Players hit by these projectiles will not be tagged as in combat.",
            "# For a full list of entity types, visit: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html"
        })
        public List<EntityType> ignoredProjectileTypes = List.of(
            EntityType.ENDER_PEARL,
            EntityType.EGG
        );
    }

    @Comment({
        " ",
        "# Customize the messages displayed by the plugin.",
        "# Modify these to change the text and formatting of notifications and alerts."
    })
    public Messages messages = new Messages();

    public static class Messages extends OkaeriConfig {

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
            .actionBar("Combat ends in: <white>{TIME}</white></gradient>")
            .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 2.0F, 1.0F)
            .build();

        @Comment({
            "# Message displayed when a player lacks permission to execute a command.",
            "# The {PERMISSION} placeholder is replaced with the required permission."
        })
        public Notice noPermission = Notice.chat("<gradient:red:dark_red>You don't have permission <dark_gray>(<gray>{PERMISSION}</gray>)</dark_gray> to perform this command!</gradient>");

        @Comment({
            "# Message displayed when a specified player is not found.",
            "# This message is shown when a command targets a player who is not online or does not exist."
        })
        public Notice playerNotFound = Notice.chat("<gradient:#ff0000:#ff6b6b>The specified player could not be found!</gradient>");

        @Comment({
            "# Message displayed when a player enters combat.",
            "# This message warns the player not to leave the server while in combat."
        })
        public Notice playerTagged = Notice.chat("<gradient:red:yellow>⚠ <white>You are in combat!</white> <u>Do not leave the server!</gradient>");

        @Comment({
            "# Message displayed when a player leaves combat.",
            "# This message informs the player that they can safely leave the server."
        })
        public Notice playerUntagged = Notice.chat("<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</u></gradient>");

        @Comment({
            "# Broadcast message displayed when a player logs out during combat.",
            "# The {PLAYER} placeholder is replaced with the player's name."
        })
        public Notice playerLoggedOutDuringCombat = Notice.chat("<gradient:red:dark_red>⚠ <white>{PLAYER}</white> logged off during combat!</gradient>");

        @Comment({
            "# Message displayed when a player attempts to use a disabled command during combat.",
            "# This message informs the player that the command is prohibited while in combat."
        })
        public Notice commandDisabledDuringCombat = Notice.chat("<gradient:red:yellow>⚠ <white>Command blocked!</white> Cannot use this during combat!</gradient>");

        @Comment({
            "# Message displayed when a player uses a command with incorrect arguments.",
            "# The {USAGE} placeholder is replaced with the correct command syntax."
        })
        public Notice invalidCommandUsage = Notice.chat("<gray>Usage: <gradient:gold:yellow>{USAGE}</gradient>");

        @Comment({
            "# Message displayed when a player attempts to open their inventory during combat.",
            "# This message informs the player that inventory access is blocked while in combat."
        })
        public Notice inventoryBlockedDuringCombat = Notice.chat("<gradient:red:dark_red>⚠ <white>Inventory access</white> is restricted during combat!</gradient>");

        @Comment({
            "# Message displayed when a player attempts to place a block during combat.",
            "# The {MODE} placeholder is replaced with the block placement mode (ABOVE/BELOW).",
            "# The {Y} placeholder is replaced with the Y coordinate set in the config."
        })
        public Notice blockPlacingBlockedDuringCombat = Notice.chat("<gradient:red:yellow>⚠ Block placement <white>{MODE} Y:{Y}</white> is restricted!</gradient>");

        @Comment({
            "# Message displayed when a player attempts to enter a restricted region during combat.",
            "# This message informs the player that they cannot enter the region while in combat."
        })
        public Notice cantEnterOnRegion = Notice.chat("<gradient:red:dark_red>⚠ Restricted area!</gradient> <white>Cannot enter during combat!</white>");

        public static class AdminMessages extends OkaeriConfig {
            @Comment({
                "# Message displayed when the console attempts to use a player-only command.",
                "# This message informs the console that the command is not available for non-players."
            })
            public Notice onlyForPlayers = Notice.chat("<gradient:red:dark_red>❌ This command is player-only!</gradient>");

            @Comment({
                "# Message displayed to an admin when they tag a player.",
                "# The {PLAYER} placeholder is replaced with the tagged player's name."
            })
            public Notice adminTagPlayer = Notice.chat("<gradient:#00b3ff:#0066ff>⚔ <gray>Tagged player:</gray> <white>{PLAYER}</white></gradient>");

            @Comment({
                "# Message displayed when an admin tags multiple players.",
                "# The {FIRST_PLAYER} and {SECOND_PLAYER} placeholders are replaced with the players' names."
            })
            public Notice adminTagMultiplePlayers = Notice.chat("<gradient:#00b3ff:#0066ff>⚔ <gray>Tagged:</gray> <white>{FIRST_PLAYER}</white> <gray>and</gray> <white>{SECOND_PLAYER}</white></gradient>");

            @Comment({
                "# Message displayed to an admin when they remove a player from combat.",
                "# The {PLAYER} placeholder is replaced with the player's name."
            })
            public Notice adminUntagPlayer = Notice.chat("<gradient:#00ff88:#00b300>✌ <gray>Removed</gray> <white>{PLAYER}</white> <gray>from combat</gray></gradient>");

            @Comment({
                "# Message displayed when an admin attempts to untag a player who is not in combat.",
                "# This message informs the admin that the player is not currently in combat."
            })
            public Notice adminPlayerNotInCombat = Notice.chat("<gradient:red:dark_red>❌ <white>{PLAYER}</white> is not in combat!</gradient>");

            @Comment({
                "# Message displayed when a player is in combat.",
                "# The {PLAYER} placeholder is replaced with the player's name."
            })
            public Notice playerInCombat = Notice.chat("<gradient:#ff6666:#ff0000>⚔ <white>{PLAYER}</white> <gray>is in combat!</gray></gradient>");

            @Comment({
                "# Message displayed when a player is not in combat.",
                "# The {PLAYER} placeholder is replaced with the player's name."
            })
            public Notice playerNotInCombat = Notice.chat("<gradient:#00ff00:#00b300>✌ <white>{PLAYER}</white> <gray>is safe</gray></gradient>");

            @Comment({
                "# Message displayed when an admin attempts to tag themselves.",
                "# This message informs the admin that they cannot tag themselves."
            })
            public Notice adminCannotTagSelf = Notice.chat("<gradient:red:dark_red>❌ Cannot tag yourself!</gradient>");

            @Comment({
                "# Message displayed when an admin disables combat tagging for themselves.",
                "# The {TIME} placeholder is replaced with the remaining fight time."
            })
            public Notice adminTagOutSelf = Notice.chat("<gradient:#00b3ff:#0066ff>🛡 <gray>Self-protection active for</gray> <white>{TIME}</white></gradient>");

            @Comment({
                "# Message displayed when an admin disables combat tagging for another player.",
                "# The {PLAYER} placeholder is replaced with the player's name.",
                "# The {TIME} placeholder is replaced with the remaining fight time."
            })
            public Notice adminTagOut = Notice.chat("<gradient:#00b3ff:#0066ff>🛡 <gray>Protected</gray> <white>{PLAYER}</white> <gray>for</gray> <white>{TIME}</white></gradient>");

            @Comment({
                "# Message displayed to a player when their combat tagging is disabled.",
                "# The {TIME} placeholder is replaced with the remaining fight time."
            })
            public Notice playerTagOut = Notice.chat("<gradient:#00ff88:#00b300>🛡 <gray>Protection active for</gray> <white>{TIME}</white></gradient>");

            @Comment({
                "# Message displayed when an admin reenables combat tagging for a player.",
                "# The {PLAYER} placeholder is replaced with the player's name."
            })
            public Notice adminTagOutOff = Notice.chat("<gradient:#00ff88:#00b300>✌ <gray>Re-enabled tagging for</gray> <white>{PLAYER}</white></gradient>");

            @Comment({
                "# Message displayed to a player when their combat tagging is reenabled.",
                "# This message informs the player that they can now be tagged again."
            })
            public Notice playerTagOutOff = Notice.chat("");

            @Comment({
                "# Message displayed when an admin attempts to tag a player who has tag-out enabled.",
                "# This message informs the admin that the player cannot be tagged at this time."
            })
            public Notice adminTagOutCanceled = Notice.chat("<gradient:red:dark_red>❌ Player has tag-out protection!</gradient>");
        }
    }
}
