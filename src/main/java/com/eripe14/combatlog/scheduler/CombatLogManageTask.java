package com.eripe14.combatlog.scheduler;

import com.eripe14.combatlog.bukkit.util.DurationUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import com.eripe14.combatlog.message.MessageAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class CombatLogManageTask implements Runnable {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;
    private final Server server;
    private final MessageAnnouncer messageAnnouncer;

    public CombatLogManageTask(CombatLogManager combatLogManager, MessageConfig messageConfig, Server server, MessageAnnouncer messageAnnouncer) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
        this.server = server;
        this.messageAnnouncer = messageAnnouncer;
    }

    @Override
    public void run() {
        for (UUID uuid : this.combatLogManager.getPlayersInCombat()) {
            Optional<Duration> leftTimeOptional = this.combatLogManager.getLeftTime(uuid);

            if (leftTimeOptional.isEmpty()) {
                continue;
            }

            Duration leftTime = leftTimeOptional.get();

            Player player = this.server.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            if (leftTime.isZero() || leftTime.isNegative()) {
                this.combatLogManager.remove(uuid);

                this.messageAnnouncer.sendMessage(player.getUniqueId(), this.messageConfig.unTagPlayer);

                continue;
            }

            Formatter formatter = new Formatter();

            formatter.register("%TIME%", DurationUtil.format(leftTime));

            this.messageAnnouncer.sendActionBar(uuid, formatter.format(this.messageConfig.combatLogDuration));
        }
    }

}
