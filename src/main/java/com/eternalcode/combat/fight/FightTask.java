package com.eternalcode.combat.fight;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NotificationAnnouncer;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;

public class FightTask implements Runnable {

    private final FightManager fightManager;
    private final PluginConfig config;
    private final Server server;
    private final NotificationAnnouncer announcer;

    public FightTask(FightManager fightManager, PluginConfig config, Server server, NotificationAnnouncer announcer) {
        this.fightManager = fightManager;
        this.config = config;
        this.server = server;
        this.announcer = announcer;
    }

    @Override
    public void run() {
        for (FightTag fightTag : this.fightManager.getFights()) {
            Player player = this.server.getPlayer(fightTag.getTaggedPlayer());

            if (player == null) {
                return;
            }

            if (!fightTag.isExpired()) {
                Duration remaining = fightTag.getRemainingDuration();

                Formatter formatter = new Formatter()
                    .register("{TIME}", DurationUtil.format(remaining));

                String format = formatter.format(this.config.messages.combatFormat);
                this.announcer.send(player, this.config.settings.combatNotificationType, format);

                continue;
            }

            this.fightManager.untag(fightTag.getTaggedPlayer());
            this.announcer.send(player, this.config.settings.combatNotificationType, this.config.messages.unTagPlayer);
        }
    }
}
