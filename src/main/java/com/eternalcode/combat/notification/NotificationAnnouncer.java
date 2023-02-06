package com.eternalcode.combat.notification;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.BiConsumer;

public final class NotificationAnnouncer {

    private final static Map<NotificationType, BiConsumer<Audience, Component>> NOTIFICATION_HANDLERS = Map.of(
        NotificationType.CHAT, Audience::sendMessage,
        NotificationType.ACTION_BAR, Audience::sendActionBar,
        NotificationType.TITLE, (audience, component) -> audience.showTitle(Title.title(component, Component.empty())),
        NotificationType.SUBTITLE, (audience, component) -> audience.showTitle(Title.title(Component.empty(), component))
    );

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void send(CommandSender commandSender, NotificationType notificationType, String text) {
        Audience audience = this.audience(commandSender);
        Component component = this.miniMessage.deserialize(text);

        BiConsumer<Audience, Component> handler = NOTIFICATION_HANDLERS.get(notificationType);

        if (handler == null) {
            return;
        }

        handler.accept(audience, component);
    }

    public void sendMessage(CommandSender commandSender, String text) {
        Audience audience = this.audience(commandSender);

        audience.sendMessage(this.miniMessage.deserialize(text));
    }

    private Audience audience(CommandSender sender) {
        if (sender instanceof Player player) {

            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }
}
