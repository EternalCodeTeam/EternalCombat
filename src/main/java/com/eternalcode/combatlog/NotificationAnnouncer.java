package com.eternalcode.combatlog;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NotificationAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    private Audience audience(CommandSender sender)  {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }

    public void sendMessage(Player player, String message) {
        this.audience(player).sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendActionBar(Player player, String message) {
        this.audience(player).sendActionBar(this.miniMessage.deserialize(message));
    }

}
