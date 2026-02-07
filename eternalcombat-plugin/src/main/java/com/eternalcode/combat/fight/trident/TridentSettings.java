package com.eternalcode.combat.fight.trident;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
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

    @Comment("# Set to true so the users will get combat log when they use riptide")
    public boolean tridentResetsTimerEnabled = false;

    @Comment({
        "# Should riptide enchantment be on cooldown?",
        "# Setting this option to 3s will make players wait 3 seconds between trident throws",
        "# Setting this to 0s will remove cooldown"
    })
    public Duration tridentRiptideDelay = Duration.ofSeconds(10);

    @Comment("# Message shown when riptide is blocked during combat")
    public Notice tridentRiptideBlocked = BukkitNotice.builder()
        .chat("<red>Using riptide is prohibited during combat!")
        .build();

    @Comment({
        "# Message sent to the player when riptide is on cooldown",
        "# Available placeholder: {TIME} - remaining time left to use riptide again"
    })
    public Notice tridentRiptideOnCooldown = BukkitNotice.builder()
        .chat("<red>You must wait {TIME} before next riptide!")
        .build();
}
