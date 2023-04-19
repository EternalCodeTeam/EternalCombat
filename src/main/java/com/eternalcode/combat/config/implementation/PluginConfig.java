package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.config.ReloadableConfig;
import com.eternalcode.combat.fight.FightCommandMode;
import com.eternalcode.combat.notification.NotificationType;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    @Description("# Do you want to change the plugin settings?")
    public Settings settings = new Settings();

    @Description({ " ", "# Do you want to change the plugin messages?" })
    public Messages messages = new Messages();

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }

    @Contextual
    public static class Settings {

        @Description("# Whether the player after entering the server should receive information about the new version of the plugin?")
        public boolean receiveUpdates = true;

        @Description("# The length of time the combat is to last")
        public Duration combatLogTime = Duration.ofSeconds(20);

        @Description("# List of worlds to ignore")
        public List<String> disabledWorlds = new ImmutableList.Builder<String>()
            .add("your_world")
            .build();

        @Description("# Combat log notification type, available types: ACTION_BAR, CHAT, TITLE, SUBTITLE")
        public NotificationType combatNotificationType = NotificationType.ACTION_BAR;

        @Description("# Command blocking mode, available modes: WHITELIST, BLACKLIST")
        public FightCommandMode fightCommandMode = FightCommandMode.BLACKLIST;

        @Description({
            "# List of commands based of the mode above",
            "# Based on BLACKLIST mode, all commands in the list is blocked, and all others are allowed",
            "# Based on WHITELIST mode, all commands in the list is allowed, and all others are blocked",
        })
        public List<String> fightCommandsList = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

        @Description("# Block the opening of inventory?")
        public boolean blockingInventories = true;

        @Description("# Whether to block the placement of blocks?")
        public boolean blockPlace = true;

        @Description("# From which level should place blocks be blocked?")
        public int blockPlaceLevel = 40;

        @Description("# Should the option below be enabled?")
        public boolean enableDamageCauses = true;

        @Description({
            "# After what type of damage the player should get a combat log?",
            "# You can find a list of all stocks here: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html",
            "# If you don't want the combatlog to be given to players for a certain damage type, simply remove it from this list"
        })
        public List<EntityDamageEvent.DamageCause> damageCauses = new ImmutableList.Builder<EntityDamageEvent.DamageCause>()
            .add(EntityDamageEvent.DamageCause.LAVA)
            .add(EntityDamageEvent.DamageCause.CONTACT)
            .add(EntityDamageEvent.DamageCause.FIRE)
            .add(EntityDamageEvent.DamageCause.FIRE_TICK)
            .build();
    }

    @Contextual
    public static class Messages {

        @Description("# Do you want to change the admin messages?")
        public Admin admin = new Admin();

        @Description({ " ", "# Combat log message format, eg. on the actionbar (you can use {TIME} variable to display the time left in combat)" })
        public String combatFormat = "&dCombat ends in: &f{TIME}";

        @Description("# Message sent when the player does not have permission to perform a command")
        public String noPermission = "&cYou don't have permission to perform this command!";

        @Description("# Message sent when the player is not in combat")
        public String cantFindPlayer = "&cThe specified player could not be found!";

        @Description("# Message sent when the player enter to combat")
        public String tagPlayer = "&cYou are in combat, do not leave the server!";

        @Description("# Message sent when the player leave combat")
        public String unTagPlayer = "&aYou are no longer in combat! You can safely leave the server.";

        @Description("# This is broadcast when the player is in combat and logs out")
        public String playerLoggedInCombat = "&c{PLAYER} logged off during the fight!";

        @Description({
            "# Message sent when the player is in combat and tries to use a disabled command",
            "# you can configure the list of disabled commands in the blockedCommands section of the config.yml file"
        })
        public String cantUseCommand = "&cUsing this command during combat is prohibited!";

        @Description("# Message sent when player tries to use a command with invalid arguments")
        public String invalidUsage = "&7Correct usage: &e{USAGE}.";

        @Description("# Message sent when player tries to open inventory, but the inventory open is blocked")
        public String inventoryBlocked = "&cYou cannot open this inventory during combat!";

        @Description("# Message sent when player tries to place a block, but the block place is blocked")
        public String blockPlaceBlocked = "&cYou cannot place blocks during combat below 40 blocks!";

        @Contextual
        public static class Admin {
            @Description("# Message sent when the configuration is reloaded")
            public String reload = "&aConfiguration has been successfully reloaded!";

            @Description("# Message sent when console tries to use a command that is only for players")
            public String onlyForPlayers = "&cThis command is only available to players!";

            @Description("# Message sent to admin when they tag a player")
            public String adminTagPlayer = "&7You have tagged &e{PLAYER}";

            @Description("# Message sent when a player is tagged by an admin")
            public String adminTagPlayerMultiple = "&7You have tagged &e{FIRST_PLAYER}&7 and &e{SECOND_PLAYER}&7.";

            @Description("# Message sent to admin when they remove a player from combat")
            public String adminUnTagPlayer = "&7You have removed &e{PLAYER}&7 from the fight.";

            @Description("# Message sent when the player is not in combat")
            public String adminPlayerIsNoInCombat = "&cThis player is not in combat!";

            @Description("# Message sent when the player is in combat")
            public String playerInCombat = "&c{PLAYER} is in the middle of a fight!";

            @Description("# Message sent when a player is not in combat")
            public String adminPlayerNotInCombat = "&a{PLAYER} is not in combat";

            @Description("# Message sent when admin tries to tag themselves")
            public String adminCantTagSelf = "&cYou cannot tag yourself!";
        }
    }
    
}
