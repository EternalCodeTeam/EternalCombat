package com.eternalcode.combat.config.implementation;

import com.eternalcode.combat.config.ReloadableConfig;
import com.eternalcode.combat.notification.NotificationType;
import com.google.common.collect.ImmutableList;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class PluginConfig implements ReloadableConfig {

    @Description("# Do you want to change the plugin messages?")
    public Messages messages = new Messages();

    @Description("# Do you want to change the plugin settings?")
    public Settings settings = new Settings();

    @Contextual
    public static class Settings {
        @Description({ " ", "# Whether the player after entering the server should receive information about the new version of the plugin?" })
        public boolean receiveUpdates = true;

        @Description("# The length of time the combat is to last")
        public Duration combatLogTime = Duration.ofSeconds(20);

        @Description("# Combat log notification type, available types: ACTION_BAR, CHAT, TITLE")
        public NotificationType combatNotificationType = NotificationType.ACTION_BAR;

        @Description({ " ", "# Blocked commands that the player will not be able to use during combat" })
        public List<String> blockedCommands = new ImmutableList.Builder<String>()
            .add("gamemode")
            .add("tp")
            .build();

        @Description({ " ", "# Block the opening of inventory?" })
        public boolean blockingInventories = true;

        @Description({ " ", "# Whether to block the placement of blocks?" })
        public boolean blockPlace = true;

        @Description({ " ", "# From which level should place blocks be blocked?" })
        public int blockPlaceLevel = 40;
    }

    @Contextual
    public static class Messages {
        public String onlyForPlayers = "&cThis command is only available to players!";
        public String noPermission = "&cYou don't have permission to perform this command!";
        public String cantFindPlayer = "&cThe specified player could not be found!";
        public String combatFormat = "&dCombat ends in: &f{TIME}";
        public String tagPlayer = "&cYou are in combat, do not leave the server!";
        public String unTagPlayer = "&aYou are no longer in combat! You can safely leave the server.";
        public String cantUseCommand = "&cUsing this command during combat is prohibited!";
        public String adminTagPlayer = "&7You have tagged &e{PLAYER}";
        public String adminTagPlayerMultiple = "&7You have tagged &e{FIRST_PLAYER}&7 and &e{SECOND_PLAYER}&7.";
        public String adminUnTagPlayer = "&7You have removed &e{PLAYER} from fight.";
        public String invalidUsage = "&7Correct usage: &e{USAGE}.";
        public String inventoryBlocked = "&cYou cannot open this inventory during combat!";
        public String blockPlaceBlocked = "&cYou cannot place blocks during combat below 40 blocks!";
        public String inCombat = "&cYou are in combat!";
        public String notInCombat = "&aYou are not in combat!";
        public String reload = "&aConfiguration successfully reloaded!";
        public String cantTagSelf = "&cYou cannot tag yourself!";
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }
}
