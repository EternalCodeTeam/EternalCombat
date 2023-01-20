package com.eternalcode.combatlog;

import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class NotificationAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void announceActionBar(UUID uniqueId, String message) {
        this.audienceProvider.player(uniqueId).sendActionBar(this.miniMessage.deserialize(message));
    }

    public void announceMessage(UUID uniqueId, String message) {
        this.audienceProvider.player(uniqueId).sendMessage(this.miniMessage.deserialize(message));
    }

    public void announceMessage(CommandSender commandSender, String message) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            this.audienceProvider.player(player.getUniqueId()).sendMessage(this.miniMessage.deserialize(message));
            return;
        }

        this.audienceProvider.console().sendMessage(this.miniMessage.deserialize(message));
    }
}
