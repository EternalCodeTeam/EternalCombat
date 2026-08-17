package com.eternalcode.combat.fight.controller;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.notification.NoticeService;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FightMessageController implements Listener {

    private final FightManager fightManager;
    private final NoticeService noticeService;
    private final PluginConfig config;
    private final Server server;

    public FightMessageController(FightManager fightManager, NoticeService noticeService, PluginConfig config, Server server) {
        this.fightManager = fightManager;
        this.noticeService = noticeService;
        this.config = config;
        this.server = server;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTag(FightTagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());

        if (player == null) {
            throw new IllegalStateException("Player cannot be null!");
        }

        if (this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        String opponent = this.resolveOpponentName(event.getTagger(), player.getUniqueId());

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(this.config.messagesSettings.playerTagged)
            .placeholder("{OPPONENT}", opponent)
            .send();
    }

    private String resolveOpponentName(UUID tagger, UUID taggedPlayer) {
        return Optional.ofNullable(tagger)
            .filter(uuid -> !uuid.equals(taggedPlayer))
            .map(this.server::getPlayer)
            .map(Player::getName)
            .orElse(this.config.messagesSettings.unknownPlayerPlaceholder);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onUnTag(FightUntagEvent event) {
        Player player = this.server.getPlayer(event.getPlayer());

        if (player == null) {
            throw new IllegalStateException("Player cannot be null!");
        }

        this.noticeService.create()
            .player(player.getUniqueId())
            .notice(this.config.messagesSettings.playerUntagged)
            .send();

    }
}
