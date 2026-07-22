package com.eternalcode.combat.fight.spear;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class SpearSettings extends OkaeriConfig {

    @Comment("# Should spears have lunge cooldown or delay between uses? True for cooldown, False for vanilla mechanics")
    public boolean lungeCooldown = false;

    @Comment("# Should cooldown be applied only during fights")
    public boolean onlyForFight = true;

    @Comment("# Duration of cooldown")
    public Duration lungeCooldownDuration = Duration.ofSeconds(5);

    @Comment("# Should milliseconds be used for last second of cooldown for more precise time")
    public boolean useMillis = true;

    @Comment({
            "# Notice sent to the players that try to use spears before cooldown ends.",
            "# Placeholder: {TIME} - time left of cooldown"
    })
    public Notice lungeOnCooldown = Notice.builder().actionBar("<dark_red>Spear cannot be used for next <red>{TIME}</red></dark_red>")
        .build();

}
