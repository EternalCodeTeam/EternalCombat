package com.eternalcode.combat.fight.pearl;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class FightPearlSettings extends OkaeriConfig {

    @Comment({ "# Is pearl damage to be enabled?", "# This will work globally" })
    public boolean pearlThrowDamageEnabled = true;

    @Comment({
        "# Set true, If you want to disable throwing pearls during combat",
        "# This will work globally, but can be overridden by region settings"
    })
    public boolean pearlThrowDisabledDuringCombat = true;

    @Comment("# Set true, If you want add cooldown to pearls")
    public boolean pearlCooldownEnabled = false;

    @Comment("# Set true, If you want to reset timer when player throws ender pearl")
    public boolean pearlResetsTimer = true;

    @Comment({
        "# Block throwing pearls with delay?",
        "# If you set this to for example 3s, player will have to wait 3 seconds before throwing another pearl"
    })
    public Duration pearlThrowDelay = Duration.ofSeconds(3);

    @Comment("# Message sent when player tries to throw ender pearl, but are disabled")
    public Notice pearlThrowBlockedDuringCombat = BukkitNotice.builder()
        .chat("<red>Throwing ender pearls is prohibited during combat!")
        .build();

    @Comment("# Message sent when player tries to throw ender pearl, but has delay")
    public Notice pearlThrowBlockedDelayDuringCombat = BukkitNotice.builder()
        .chat("<red>You must wait {TIME} before next throw!")
        .build();

}
