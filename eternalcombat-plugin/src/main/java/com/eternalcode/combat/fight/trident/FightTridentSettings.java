package com.eternalcode.combat.fight.trident;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class FightTridentSettings extends OkaeriConfig {

    @Comment({
        "# Set to true to disable riptide usage during combat",
        "# This setting works globally, but can be overridden by region settings"
    })
    public boolean tridentRiptideDisabledDuringCombat = true;

    @Comment("# Set to true so throwing trident will result in cooldown - delay between uses")
    public boolean tridentCooldownEnabled = false;

    @Comment("# Set to true so the users will get combat log when they use riptide")
    public boolean tridentResetsTimerEnabled = false;

    @Comment({
        "# Should riptide enchantment be on cooldown?",
        "# Setting this option to 3s will make players wait 3 seconds between trident throws"
    })
    public Duration tridentRiptideDelay = Duration.ofSeconds(3);

    @Comment("# Message shown when riptide is blocked during combat")
    public Notice tridentRiptideBlockedDuringCombat = BukkitNotice.builder()
        .chat("<red>Using riptide is prohibited during combat!")
        .build();

    @Comment("# Message sent to the player when riptide is on cooldown")
    public Notice tridentRiptideBlockedDelayDuringCombat = BukkitNotice.builder()
        .chat("<red>You must wait {TIME} before next usage!")
        .build();
}
