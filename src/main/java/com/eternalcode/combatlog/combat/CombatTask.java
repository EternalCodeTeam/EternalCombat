package com.eternalcode.combatlog.combat;

import com.eternalcode.combatlog.NotificationAnnouncer;
import com.eternalcode.combatlog.config.implementation.MessageConfig;
import com.eternalcode.combatlog.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class CombatTask implements Runnable {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;

    public CombatTask(CombatManager combatManager, MessageConfig messageConfig, Server server, NotificationAnnouncer notificationAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void run() {
        for (Combat combat : this.combatManager.getCombats()) {
            Player player = this.server.getPlayer(combat.getUuid());

            if (player == null) {
                return;
            }

            Instant now = Instant.now();
            Instant remainingTime = combat.getEndOfCombatLog();

            UUID playerUniqueId = player.getUniqueId();

            if (now.isBefore(remainingTime)) {
                Duration between = Duration.between(now, remainingTime);

                Formatter formatter = new Formatter()
                        .register("{TIME}", DurationUtil.format(between));

                this.notificationAnnouncer.announceActionBar(playerUniqueId, formatter.format(this.messageConfig.combatActionBar));

                continue;
            }

            this.combatManager.remove(combat.getUuid());
            this.notificationAnnouncer.announceActionBar(playerUniqueId, this.messageConfig.unTagPlayer);

        }
    }
}
