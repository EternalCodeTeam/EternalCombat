package com.eternalcode.combat.fight.trident;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class TridentSettings extends OkaeriConfig {

    @Comment({
        "# Set to true to disable riptide usage during combat",
        "# This setting works globally, but can be overridden by region settings"
    })
    public boolean tridentRiptideDisabledDuringCombat = false;

    @Comment("# Set to true so then riptide usage during combat will extend combat tag duration, works only if riptide usage is not disabled")
    public boolean tridentRiptideExtendsCombatTag = false;

    @Comment({
        "# Should riptide enchantment be on cooldown during combat?",
        "# Setting this option to 3s will make players wait 3 seconds between trident throws",
        "# Setting this to 0s or below will remove cooldown"
    })
    public Duration tridentRiptideDelay = Duration.ofSeconds(10);

    @Comment("# Message shown when riptide is blocked during combat")
    public Notice tridentRiptideBlocked = Notice.builder()
        .chat("<red>Using riptide is prohibited during combat!")
        .build();

    @Comment({
        "# Message sent to the player when riptide is on cooldown",
        "# Available placeholder: {TIME} - remaining time left to use riptide again"
    })
    public Notice tridentRiptideOnCooldown = Notice.builder()
        .chat("<red>You must wait {TIME} before next riptide!")
        .build();
}
