package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public class FightTask implements Runnable {

    private final Server server;
    private final PluginConfig config;
    private final FightManager fightManager;
    private final NotificationAnnouncer announcer;

    public FightTask(Server server, PluginConfig config, FightManager fightManager,  NotificationAnnouncer announcer) {
        this.server = server;
        this.config = config;
        this.fightManager = fightManager;
        this.announcer = announcer;
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

            this.announcer.create()
                .player(player.getUniqueId())
                .notice(this.config.messages.combatNotification)
                .placeholder("{TIME}", DurationUtil.format(remaining))
                .send();

        }
    }
}
