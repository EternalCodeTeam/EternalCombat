package com.eternalcode.combat.fight.death;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class DeathSettings extends OkaeriConfig {

    @Comment("Should lightning strike when a player dies")
    public boolean lightning = true;
}
