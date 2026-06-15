package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class SignEditingSettings extends OkaeriConfig {

    @Comment({
        "# Prevent players from editing signs during combat.",
        "# Disabled by default; created for sign traps where sign editing blocks ender pearl throws.",
        "# Powstalo na potrzebe trapow z tabliczkami."
    })
    public boolean disableSignEditingDuringCombat = false;
}
