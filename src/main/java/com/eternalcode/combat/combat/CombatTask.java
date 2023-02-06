package com.eternalcode.combat.combat;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;

public class CombatTask implements Runnable {

    private final CombatManager combatManager;
    private final PluginConfig config;
    private final Server server;
    private final NotificationAnnouncer announcer;

    public CombatTask(CombatManager combatManager, PluginConfig config, Server server, NotificationAnnouncer announcer) {
        this.combatManager = combatManager;
        this.config = config;
        this.server = server;
        this.announcer = announcer;
    }

    @Override
    public void run() {
        for (CombatTag combatTag : this.combatManager.getCombats()) {
            Player player = this.server.getPlayer(combatTag.getTaggedPlayer());

            if (player == null) {
                return;
            }

            if (!combatTag.isExpired()) {
                Duration remaining = combatTag.getRemainingDuration();

                Formatter format = new Formatter()
                    .register("{TIME}", DurationUtil.format(remaining));

                this.announcer.send(player, this.config.settings.combatNotificationType, format.format(this.config.messages.combatFormat));

                continue;
            }

            this.combatManager.untag(combatTag.getTaggedPlayer());
            this.announcer.send(player, this.config.settings.combatNotificationType, this.config.messages.unTagPlayer);
        }
    }
}
