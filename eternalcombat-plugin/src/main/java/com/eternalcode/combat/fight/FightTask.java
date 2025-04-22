package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class FightTask implements Runnable {

    private final Server server;
    private final PluginConfig config;
    private final FightManager fightManager;
    private final NoticeService noticeService;

    public FightTask(Server server, PluginConfig config, FightManager fightManager,  NoticeService noticeService) {
        this.server = server;
        this.config = config;
        this.fightManager = fightManager;
        this.noticeService = noticeService;
    }

    @Override
    public void run() {
        for (FightTag fightTag : this.fightManager.getFights()) {
            Player player = this.server.getPlayer(fightTag.getTaggedPlayer());

            if (player == null) {
                continue;
            }

            UUID playerUniqueId = player.getUniqueId();

            if (fightTag.isExpired()) {
                this.fightManager.untag(playerUniqueId, CauseOfUnTag.TIME_EXPIRED);
                return;
            }

            Duration remaining = fightTag.getRemainingDuration();

            this.noticeService.create()
                .player(player.getUniqueId())
                .notice(this.config.messagesSettings.combatNotification)
                .placeholder("{TIME}", DurationUtil.format(remaining, this.config.messagesSettings.withoutMillis))
                .send();

        }
    }
}
