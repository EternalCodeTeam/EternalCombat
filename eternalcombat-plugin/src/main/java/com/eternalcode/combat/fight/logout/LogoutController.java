package com.eternalcode.combat.fight.logout;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogoutController implements Listener {

    private final FightManager fightManager;
    private final LogoutService logoutService;
    private final NoticeService noticeService;
    private final PluginConfig config;


    public LogoutController(FightManager fightManager, LogoutService logoutService, NoticeService noticeService, PluginConfig config) {
        this.fightManager = fightManager;
        this.logoutService = logoutService;
        this.noticeService = noticeService;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH)
    void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!this.fightManager.isInCombat(player.getUniqueId())) {
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
