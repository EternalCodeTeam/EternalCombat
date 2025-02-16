package com.eternalcode.combat.fight.knockback;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class KnockbackSettings extends OkaeriConfig {

    @Comment({ "# Time after which the player will be force knocked back outside the safe zone" })
    public Duration forceKnockbackDuration = Duration.ofSeconds(1);

}
