package com.eripe14.combatlog.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

@Header({"### EternalCombatLog (Message-Config) ###"})
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MessageConfig extends OkaeriConfig {

    public String noPermission = "&4Nie masz permisji do wykonania tej komendy!";

    public String cantFindPlayer = "&4Nie znaleziono podanego gracza!";

    public String combatLogDuration = "&dCombatLog &7minie za: &d%TIME%";

    public String tagPlayer = "&7Zdobyłeś &dCombatLog&7, wyjście z serwera wiążę się ze śmiercią!";

    public String unTagPlayer = "&7Straciłeś &dCombatLog&7, możesz teraz wyjść z serwera.";

    public String cantUseCommand = "&4Nie możesz użyć komendy podczas &dCombatLoga&4!";

    @Comment("Sekcja z wiadomościami dla administracji.")

    public String adminTagPlayer = "&7Nadałeś &dCombatLoga&7, graczu &d%FIRST_PLAYER%&7 oraz &d%SECOND_PLAYER%&7.";

    public String adminUnTagPlayer = "&7Odebrałeś &dCombatLoga&7, graczu &d%PLAYER%&7 oraz jego przeciwnikowi.";

}
