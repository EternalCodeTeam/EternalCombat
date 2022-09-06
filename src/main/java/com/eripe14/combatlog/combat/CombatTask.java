package com.eripe14.combatlog.combat;

import com.eripe14.combatlog.combat.Combat;
import com.eripe14.combatlog.util.DurationUtil;
import com.eripe14.combatlog.combat.CombatManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;

public class CombatTask implements Runnable {

    private final CombatManager combatManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public CombatTask(CombatManager combatManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatManager = combatManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
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


            if (now.isBefore(remainingTime)) {
                Duration between = Duration.between(now, remainingTime);

                Formatter formatter = new Formatter();
                formatter.register("{TIME}", DurationUtil.format(between));

                this.messageAnnouncer.sendActionBar(player.getUniqueId(), formatter.format(this.messageConfig.combatActionBar));

                continue;
            }

            this.combatManager.remove(combat.getUuid());

            this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.unTagPlayer);

        }
    }
}
