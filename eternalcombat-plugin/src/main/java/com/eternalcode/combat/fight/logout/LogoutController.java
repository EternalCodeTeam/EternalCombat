package com.eternalcode.combat.fight.logout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.event.DynamicListener;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutController implements DynamicListener<PlayerQuitEvent> {

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

    @Override
    public void onEvent(PlayerQuitEvent event) {
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
