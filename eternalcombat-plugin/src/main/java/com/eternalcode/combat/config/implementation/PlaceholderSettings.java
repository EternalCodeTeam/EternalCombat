package com.eternalcode.combat.config.implementation;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PlaceholderSettings extends OkaeriConfig {

    @Comment("Text returned by %eternalcombat_isInCombat_formatted% placeholder when the player is in combat")
    public String isInCombatFormattedTrue = "In Combat";

    @Comment("Text returned by %eternalcombat_isInCombat_formatted% placeholder when the player is out of combat")
    public String isInCombatFormattedFalse = "Not In Combat";

}
