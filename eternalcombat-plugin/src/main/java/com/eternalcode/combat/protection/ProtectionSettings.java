package com.eternalcode.combat.protection;

import com.eternalcode.multification.notice.Notice;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.time.Duration;

public class ProtectionSettings extends OkaeriConfig {

    public boolean afterRespawnProtection = true;
    public boolean afterRespawnProtectionFromDamage = true;
    public Duration afterRespawnProtectionTime = Duration.ofSeconds(20);

    public boolean afterJoinProtection = true;
    public boolean afterJoinProtectionFromDamage = true;
    public Duration afterJoinProtectionTime = Duration.ofSeconds(20);

    public boolean firstServerJoinProtection = true;
    public boolean firstServerJoinProtectionFromDamage = true;
    public Duration firstServerJoinProtectionTime = Duration.ofMinutes(5);


    @Comment({
        "# Message displayed when a player leaves combat.",
        "# This message informs the player that they can safely leave the server."
    })
    public Notice afterRespawnPlayerProtectedMessage = Notice.chat(
        "<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</gradient>");

    @Comment({
        "# Message displayed when a player leaves combat.",
        "# This message informs the player that they can safely leave the server."
    })
    public Notice afterJoinPlayerProtectedMessage = Notice.chat(
        "<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</gradient>");

    @Comment({
        "# Message displayed when a player leaves combat.",
        "# This message informs the player that they can safely leave the server."
    })
    public Notice firstServerJoinPlayerProtectedMessage = Notice.chat(
        "<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</gradient>");

    @Comment({
        "# Message displayed when a player leaves combat.",
        "# This message informs the player that they can safely leave the server."
    })
    public Notice firstServerJoinProtectedMessage = Notice.chat(
        "<gradient:#00ff00:#00b300>✌ <white>Combat ended!</white> You can now safely leave!</gradient>");


}
