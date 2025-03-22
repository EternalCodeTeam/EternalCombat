package com.eternalcode.combat.fight.knockback;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class KnockbackSettings extends OkaeriConfig {

    @Comment({
        "# Adjust the knockback multiplier for restricted regions.",
        "# Higher values increase the knockback distance. Avoid using negative values.",
        "# A value of 1.0 typically knocks players 2-4 blocks away."
    })
    public double multiplier = 1;

    @Comment({ "# Time after which the player will be force knocked back outside the safe zone" })
    public Duration forceDelay = Duration.ofSeconds(1);

}
