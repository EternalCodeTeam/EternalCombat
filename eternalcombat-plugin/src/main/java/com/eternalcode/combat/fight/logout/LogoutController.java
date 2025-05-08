package com.eternalcode.combat.fight.logout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutController implements Listener {

    private final FightManager fightManager;
    private final LogoutService logoutService;
    private final NoticeService noticeService;
    private final PluginConfig config;

    private final Set<UUID> shouldNotPunishOnQuit = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public LogoutController(FightManager fightManager, LogoutService logoutService, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.logoutService = logoutService;
        this.noticeService = noticeService;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!this.fightManager.isInCombat(uuid)) {
            return;
        }

        String reason = event.getReason().trim();
        List<String> whitelist = this.config.combat.whitelistedKickReasons;

        if (whitelist.isEmpty()) {
            return;
        }

        for (String whitelisted : whitelist) {
            if (reason.toLowerCase().contains(whitelisted.toLowerCase())) {
                this.shouldNotPunishOnQuit.add(uuid);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onQuitLowest(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.LOWEST) {
            this.handle(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    void onQuitLow(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.LOW) {
            this.handle(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    void onQuitNormal(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.NORMAL) {
            this.handle(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onQuitHigh(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.HIGH) {
            this.handle(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    void onQuitHighest(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.HIGHEST) {
            this.handle(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onQuitMonitor(PlayerQuitEvent event) {
        if (this.config.combat.quitPunishmentEventPriority == EventPriority.MONITOR) {
            this.handle(event);
        }
    }

    void handle(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
            return;
        }

        if (this.shouldNotPunishOnQuit.remove(player.getUniqueId())) {
            return;
        }

        this.logoutService.punishForLogout(player);
        player.setHealth(0.0);

        this.noticeService.create()
            .notice(this.config.messagesSettings.playerLoggedOutDuringCombat)
            .placeholder("{PLAYER}", player.getName())
            .all()
            .send();
    }

}
