package com.eternalcode.combat.notification;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class NotificationAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void sendWithType(CommandSender commandSender, NotificationType notificationType, String text) {
        Audience audience = this.audience(commandSender);

        switch (notificationType) {
            case ACTION_BAR:
                audience.sendActionBar(this.miniMessage.deserialize(text));
                break;
            case TITLE:
                audience.showTitle(Title.title(this.miniMessage.deserialize(text), Component.empty()));
                break;
            case SUBTITLE:
                audience.showTitle(Title.title(Component.empty(), this.miniMessage.deserialize(text)));
                break;
            case CHAT:
            default: audience.sendMessage(this.miniMessage.deserialize(text));
        }
    }

    public void sendMessage(CommandSender commandSender, String text) {
        Audience audience = this.audience(commandSender);

        audience.sendMessage(this.miniMessage.deserialize(text));
    }

    private Audience audience(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
