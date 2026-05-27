package com.eternalcode.combat.notification;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.multification.adventure.AudienceConverter;
import com.eternalcode.multification.paper.PaperMultification;
import com.eternalcode.multification.translation.TranslationProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class NoticeService extends PaperMultification<PluginConfig> {

    private final PluginConfig pluginConfig;
    private final MiniMessage miniMessage;

    public NoticeService(PluginConfig pluginConfig, MiniMessage miniMessage) {
        this.pluginConfig = pluginConfig;
        this.miniMessage = miniMessage;
    }

    @Override
    protected @NotNull TranslationProvider<PluginConfig> translationProvider() {
        return locale -> this.pluginConfig;
    }

    @Override
    protected @NotNull ComponentSerializer<Component, Component, String> serializer() {
        return this.miniMessage;
    }

    @Override
    protected @NotNull AudienceConverter<CommandSender> audienceConverter() {
        return commandSender -> commandSender;

    }
}
