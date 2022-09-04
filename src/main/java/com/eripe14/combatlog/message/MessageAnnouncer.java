package com.eripe14.combatlog.message;

import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public final class MessageAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public MessageAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void sendMessage(UUID uuid, String message) {
        this.audienceProvider.player(uuid).sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendConsoleMessage(String message) {
        this.audienceProvider.console().sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendActionBar(UUID uuid, String message) {
        this.audienceProvider.player(uuid).sendActionBar(this.miniMessage.deserialize(message));
    }


}
