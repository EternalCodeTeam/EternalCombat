package com.eternalcode.combat.fight.drop;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import org.bukkit.event.EventPriority;

import java.util.List;

public class DropSettings extends OkaeriConfig {


    @Comment({
        "# The event priority at which the head drop logic should run.",
        "# Options: LOWEST, LOW, NORMAL, HIGH, HIGHEST, MONITOR",
        "# Useful if you want to control when drops are processed relative to other plugins.",
        "# Default: NORMAL"
    })
    public EventPriority dropEventPriority = EventPriority.NORMAL;

    @Comment({
        "# UNCHANGED - The default way of item drop defined by the engine",
        "# PERCENT - Drops a fixed percentage of items",
        "# PLAYERS_HEALTH - Drops inverted percentage of the player's health (i.e. if the player has, for example, 80% HP, he will drop 20% of items. Only works when the player escapes from combat by quiting game)"
    })
    public DropType dropType = DropType.UNCHANGED;

    @Comment("# What percentage of items should drop from the player? (Only if Drop Type is set to PERCENT)")
    public int dropItemPercent = 100;

    @Comment("# This option is responsible for the lowest percentage of the player that can drop (i.e. if the player leaves the game while he has 100% of his HP, the percentage of items that is set in this option will drop, if you set this option to 0, then nothing will drop from such a player)")
    public int playersHealthPercentClamp = 20;

    @Comment("# Does the drop modification affect the experience drop?")
    public boolean affectExperience = false;

    @Comment({
        "",
        "# If true, players can drop their head on death based on chance settings below."
    })
    public boolean headDropEnabled = false;

    @Comment({
        "# Chance for a head to drop on death (0-100).",
        "# Set to 0 to disable even if feature is enabled.",
        "# Example: 25.0 means 25% chance."
    })
    public double headDropChance = 0.0;

    @Comment({
        "# Only drop the head if the player was in combat at time of death."
    })
    public boolean headDropOnlyInCombat = true;

    @Comment({
        "# The display name of the dropped head.",
        "# Placeholders: {PLAYER}, {KILLER}",
        "# Example: \"{PLAYER}'s Head\""
    })
    public String headDropDisplayName = "{PLAYER}'s Head";

    @Comment({
        "# Lore lines shown on the head item.",
        "# Placeholders: {PLAYER}, {KILLER}",
        "# Set to [] to disable lore entirely."
    })
    public List<String> headDropLore = List.of(
        "Slain by {KILLER}",
        "Collected in battle"
    );
}
