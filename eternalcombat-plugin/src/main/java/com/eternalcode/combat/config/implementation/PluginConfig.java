package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.border.BorderSettings;
import com.eternalcode.combat.fight.drop.DropSettings;
import com.eternalcode.combat.fight.effect.FightEffectSettings;
import com.eternalcode.combat.fight.knockback.KnockbackSettings;
import com.eternalcode.combat.fight.pearl.FightPearlSettings;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;
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
        " ",
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

    @Comment({
        " ",
        "# Settings related to knockback during combat.",
        "# Configure knockback settings and behaviors for players in combat."
    })
    public KnockbackSettings knockback = new KnockbackSettings();

    @Comment({
        " ",
        "# Border Settings",
        "# Configure the border that appears during combat.",
    })
    public BorderSettings border = new BorderSettings();

    @Comment({
        " ",
        "# Settings related to block placement during combat.",
        "# Configure restrictions and behaviors for block placement while players are in combat."
    })
    public BlockPlacementSettings blockPlacement = new BlockPlacementSettings();

    @Comment({
        " ",
        "# Settings related to commands during combat.",
        "# Configure command restrictions and behaviors for players in combat."
    })
    public CommandSettings commands = new CommandSettings();

    @Comment({
        " ",
        "# Settings related to the plugin's admin commands and features.",
        "# Configure admin-specific settings and behaviors for the plugin."
    })
    public AdminSettings admin = new AdminSettings();

    @Comment({
        " ",
        "# Settings related to regions.",
        "# Configure region-specific settings and behaviors for combat."
    })
    public RegionSettings regions = new RegionSettings();

    @Comment({
        " ",
        "# Settings related to combat and player tagging.",
        "# Configure combat rules, and behaviors for player tagging."
    })
    public CombatSettings combat = new CombatSettings();

    @Comment({
        " ",
        "# Customize the messages displayed by the plugin.",
        "# Modify these to change the text and formatting of notifications and alerts."
    })
    public MessagesSettings messagesSettings = new MessagesSettings();

    @Comment({
        "# Player head drop settings",
        "# This section contains settings for player head drops on death"
    })
    public PlayerHeadSettings playerHead = new PlayerHeadSettings();

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
    }

    public static class PlayerHeadSettings extends OkaeriConfig {
        @Comment({
            "# Whether to enable player head drops on death",
            "# When enabled, players have a chance to drop their head when killed"
        })
        public boolean enabled = true;

        @Comment({
            "# The chance (in percentage) for a player to drop their head when killed",
            "# Value must be between 0 and 100",
            "# Example: 25.5 means 25.5% chance to drop head"
        })
        public double dropChance = 25.0;

        @Comment({
            "# Whether to only drop heads when killed by another player",
            "# If true, heads will only drop on PvP deaths",
            "# If false, heads can drop from any death (fall, mob, etc.)"
        })
        public boolean onlyOnPvPDeath = true;

        @Comment({
            "# Whether to drop the head of the killer instead of the victim",
            "# If true, the killer's head will drop",
            "# If false, the victim's head will drop"
        })
        public boolean dropKillerHead = false;
    }
}
