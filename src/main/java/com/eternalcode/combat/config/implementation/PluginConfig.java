package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.config.ReloadableConfig;
import com.eternalcode.combat.drop.DropSettings;
import com.eternalcode.combat.WhitelistBlacklistMode;
import com.eternalcode.combat.fight.pearl.FightPearlSettings;
import com.eternalcode.combat.notification.NotificationType;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    @Description("# Do you want to change the plugin settings?")
    public Settings settings = new Settings();

    @Description({" ", "# Ender pearl settings"})
    public FightPearlSettings pearl = new FightPearlSettings();

    @Description({ " ", "# Set a custom way for a player's items to drop on death (if in combat)" })
    public DropSettings dropSettings = new DropSettings();

    @Contextual
    public static class Settings {

        @Description("# Whether the player should receive information about new plugin updates upon joining the server")
        public boolean shouldReceivePluginUpdates = true;

        @Description("# The duration of combat in seconds")
        public Duration combatDuration = Duration.ofSeconds(20);

        @Description("# List of worlds to ignore")
        public List<String> worldsToIgnore = List.of(
            "your_world"
        );

        @Description("# List of regions to block")
        public List<String> blockedRegions = Collections.singletonList("your_region");

        @Description("# Set the knock multiplier for the blocked region")
        public double blockedRegionKnockMultiplier = 1.2;

        @Description("# Set the radius of the blocked region if you aren't using WorldGuard basen on default spawn region!")
        public int blockedRegionRadius = 10;

        @Description("# Release attacker after victim dies?")
        public boolean shouldReleaseAttacker = true;

        @Description("# Combat log notification type, available types: ACTION_BAR, CHAT, TITLE, SUBTITLE")
        public NotificationType notificationType = NotificationType.ACTION_BAR;

        @Description("# Command blocking mode, available modes: WHITELIST, BLACKLIST")
        public WhitelistBlacklistMode commandBlockingMode = WhitelistBlacklistMode.BLACKLIST;

        @Description({
            "# List of commands based on the mode above",
            "# Based on BLACKLIST mode, all commands in the list are blocked, and all others are allowed",
            "# Based on WHITELIST mode, all commands in the list are allowed, and all others are blocked",
        })
        public List<String> blockedCommands = List.of(
            "gamemode",
            "spawn",
            "tp"
        );

        @Description("# Block the use of elytra?")
        public boolean shouldPreventElytraUsage = true;

        @Description("# Disable the use of elytra on damage?")
        public boolean shouldElytraDisableOnDamage = true;

        @Description("# Block the opening of inventory?")
        public boolean shouldPreventInventoryOpening = true;

        @Description("# Whether to block the placement of blocks?")
        public boolean shouldPreventBlockPlacing = true;

        @Description("# The minimum Y level at which block placing is blocked")
        public int minBlockPlacingLevel = 40;

        @Description({
            "# Disable placing specific blocks?",
            "# If you want to block all blocks, enable shouldPreventBlockPlacing and make this list empty",
            "# If you want to disable placing only specific blocks, enable shouldPreventBlockPlacing and add blocks to this list above",
            "# If you want to disable this feature completely, disable shouldPreventBlockPlacing option above",
        })
        public List<Material> specificBlocksToPreventPlacing = List.of();

        @Description("# Do You want to enable combat log for non-player causes of damage? - Set to false to disable")
        public boolean shouldEnableDamageCauses = false;

        @Description("# Select the mode for damage causes, available modes: WHITELIST, BLACKLIST")
        public WhitelistBlacklistMode damageCausesMode = WhitelistBlacklistMode.WHITELIST;

        @Description({
            "# After selecting the mode above, select the causes of damage to be logged",
            "# You can find a list of all causes here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html",
            "# While using Whitelist mode the player will get a combat log after the damage from the list below",
            "# While using Blacklist mode the player will get a combat log after any damage non-listed below",
        })
        public List<EntityDamageEvent.DamageCause> damageCausesToLog = List.of(
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.CONTACT,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK
        );

        @Description({
            "# After what type of projectile entity should not tag the player as fighter?",
            "# You can find a list of all entity types here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html"
        })
        public List<EntityType> disabledProjectileEntities = List.of(
            EntityType.ENDER_PEARL
        );
    }

    @Description({ " ", "# Do you want to change the plugin messages?" })
    public Messages messages = new Messages();

    @Contextual
    public static class Messages {

        @Description("# Do you want to change the admin messages?")
        public AdminMessages admin = new AdminMessages();

        @Description({ " ", "# Combat log message format, e.g. on the actionbar (you can use {TIME} variable to display the time left in combat)" })
        public String combatFormat = "&dCombat ends in: &f{TIME}";

        @Description("# Message sent when the player does not have permission to perform a command")
        public String noPermission = "&cYou don't have permission to perform this command!";

        @Description("# Message sent when the specified player could not be found")
        public String playerNotFound = "&cThe specified player could not be found!";

        @Description("# Message sent when the player enters combat")
        public String playerTagged = "&cYou are in combat, do not leave the server!";

        @Description("# Message sent when the player leaves combat")
        public String playerUntagged = "&aYou are no longer in combat! You can safely leave the server.";

        @Description("# This is broadcast when the player is in combat and logs out")
        public String playerLoggedOutDuringCombat = "&c{PLAYER} logged off during the fight!";

        @Description({
            "# Message sent when the player is in combat and tries to use a disabled command",
            "# you can configure the list of disabled commands in the blockedCommands section of the config.yml file"
        })
        public String commandDisabledDuringCombat = "&cUsing this command during combat is prohibited!";

        @Description("# Message sent when player tries to use a command with invalid arguments")
        public String invalidCommandUsage = "&7Correct usage: &e{USAGE}";

        @Description("# Message sent when player tries to open inventory, but the inventory open is blocked")
        public String inventoryBlockedDuringCombat = "&cYou cannot open this inventory during combat!";

        @Description("# Message sent when player tries to place a block, but the block place is blocked")
        public String blockPlacingBlockedDuringCombat = "&cYou cannot place below 40Y coordinate during combat!";

        @Description("# Message sent when player tries to enter a region")
        public String cantEnterOnRegion = "&cYou can't enter on this region during combat!";

        @Contextual
        public static class AdminMessages {
            @Description("# Message sent when the configuration is reloaded")
            public String reload = "&aConfiguration has been successfully reloaded!";

            @Description("# Message sent when console tries to use a command that is only for players")
            public String onlyForPlayers = "&cThis command is only available to players!";

            @Description("# Message sent to admin when they tag a player")
            public String adminTagPlayer = "&7You have tagged &e{PLAYER}";

            @Description("# Message sent when a player is tagged by an admin")
            public String adminTagMultiplePlayers = "&7You have tagged &e{FIRST_PLAYER}&7 and &e{SECOND_PLAYER}&7.";

            @Description("# Message sent to admin when they remove a player from combat")
            public String adminUntagPlayer = "&7You have removed &e{PLAYER}&7 from the fight.";

            @Description("# Message sent when the player is not in combat")
            public String adminPlayerNotInCombat = "&cThis player is not in combat!";

            @Description("# Message sent when the player is in combat")
            public String playerInCombat = "&c{PLAYER} is currently in combat!";

            @Description("# Message sent when a player is not in combat")
            public String playerNotInCombat = "&a{PLAYER} is not currently in combat.";

            @Description("# Message sent when an admin tries to tag themselves")
            public String adminCannotTagSelf = "&cYou cannot tag yourself!";
        }
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }

}
