package com.eternalcode.combatlog.config.implementation;

import com.eternalcode.combatlog.config.ReloadableConfig;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;

public class MessageConfig implements ReloadableConfig {

    public String noPermission = "&cNie masz uprawnien do wykonania tej komendy!";

    public String cantFindPlayer = "&cNie znaleziono podanego gracza!";

    public String combatActionBar = "&dWalka kończy się za: &f{TIME}";

    public String tagPlayer = "&cJesteś podczas walki, nie wychodź z serwera!";

    public String unTagPlayer = "&aSkączyłeś walczyć!";

    public String cantUseCommand = "&cUżywanie tej komendy podczas walki jest zabronione!";

    public String adminTagPlayer = "&7Nadałeś combatLog dla &e{FIRST_PLAYER}&7 oraz &e{SECOND_PLAYER}&7.";

    public String adminUnTagPlayer = "&7Usunąłeś combatLog dla &e{FIRST_PLAYER}&7 oraz &e{SECOND_PLAYER}&7.";

    public String invalidUsage = "&7Poprawne użycie: &e{COMMAND}.";

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "messages.yml");
    }
}
