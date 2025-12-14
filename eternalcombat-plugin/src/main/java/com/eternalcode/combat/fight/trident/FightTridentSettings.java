package com.eternalcode.combat.fight.trident;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class FightTridentSettings extends OkaeriConfig {

    @Comment({
        "# Set true, If you want to disable throwing trident during combat",
        "# This will work globally, but can be overridden by region settings"
    })
    public boolean tridentThrowDisabledDuringCombat = true;

    @Comment("# Set true, If you want add cooldown to pearls")
    public boolean tridentCooldownEnabled = false;

    @Comment("# Set true, If you want to reset timer when player uses trident")
    public boolean tridentResetsTimerEnabled = true;

    @Comment({
        "# Block throwing trident with delay?",
        "# If you set this to for example 3s, player will have to wait 3 seconds before throwing another pearl"
    })
    public Duration tridentThrowDelay = Duration.ofSeconds(3);

    @Comment("# Message sent when player tries to throw trident, but are disabled")
    public Notice tridentThrowBlockedDuringCombat = BukkitNotice.builder()
        .chat("<red>Throwing trident is prohibited during combat!")
        .build();

    @Comment("# Message sent when player tries to throw ender pearl, but has delay")
    public Notice tridentThrowBlockedDelayDuringCombat = BukkitNotice.builder()
        .chat("<red>You must wait {TIME} before next throw!")
        .build();
}
