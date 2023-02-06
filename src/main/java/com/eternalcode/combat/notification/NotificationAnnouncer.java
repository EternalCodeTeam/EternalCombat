package com.eternalcode.combat.notification;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

public final class NotificationAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void sendMessage(CommandSender sender, String message) {
        Audience audience = this.audience(sender);

        audience.sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendActionBar(Player player, String message) {
        Audience audience = this.audience(player);

        audience.sendActionBar(this.miniMessage.deserialize(message));
    }

    private Audience audience(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
