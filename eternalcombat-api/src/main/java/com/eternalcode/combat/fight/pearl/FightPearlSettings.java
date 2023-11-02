package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.notification.Notification;
import com.eternalcode.combat.notification.implementation.ChatNotification;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import java.time.Duration;

public class FightPearlSettings extends OkaeriConfig {

    @Comment({ "# Is pearl damage to be enabled?", "# This will work globally" })
    public boolean pearlThrowDamageEnabled = true;

    @Comment("# Set true, If you want to lock pearls during the combat")
    public boolean pearlThrowBlocked = false;

    @Comment({
        "# Block throwing pearls with delay?",
        "# If you set this to for example 3s, player will have to wait 3 seconds before throwing another pearl",
        "# Set to 0 to disable"
    })
    public Duration pearlThrowDelay = Duration.ofSeconds(3);

    @Comment("# Message sent when player tries to throw ender pearl, but are disabled")
    public Notification pearlThrowBlockedDuringCombat = new ChatNotification("&cThrowing ender pearls is prohibited during combat!");

    @Comment("# Message sent when player tries to throw ender pearl, but has delay")
    public Notification pearlThrowBlockedDelayDuringCombat = new ChatNotification("&cYou must wait {TIME} before next throw!");

}
