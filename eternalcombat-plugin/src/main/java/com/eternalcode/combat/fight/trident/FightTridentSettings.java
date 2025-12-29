package com.eternalcode.combat.fight.trident;

import com.eternalcode.multification.bukkit.notice.BukkitNotice;
import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class FightTridentSettings extends OkaeriConfig {

    @Comment({
        "# Set to true to disable throwing tridents during combat.",
        "# This will work globally, but can be overridden by region settings"
    })
    public boolean tridentThrowDisabledDuringCombat = false;

    @Comment("# Set to true so throwing trident will result in cooldown - delay between throws")
    public boolean tridentCooldownEnabled = false;

    @Comment("# Set to true, so the users will get combat log when they throw tridents")
    public boolean tridentResetsTimer = false;

    @Comment({
        "# Should throwing trident be on cooldown?",
        "# Setting this option to 3s will make players wait 3 seconds between trident throws"
    })
    public Duration tridentThrowDelay = Duration.ofSeconds(3);

    @Comment("# Message sent to the player when throwing trident is disabled")
    public Notice tridentThrowBlockedDuringCombat = BukkitNotice.builder()
        .chat("<red>Throwing trident is prohibited during combat!")
        .build();

    @Comment("# Message marking delay between trident throws")
    public Notice tridentThrowBlockedDelayDuringCombat = BukkitNotice.builder()
        .chat("<red>You must wait {TIME} before next throw!")
        .build();
}
