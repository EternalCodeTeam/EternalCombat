package com.eripe14.combatlog.scheduler;

import com.eripe14.combatlog.bukkit.util.ChatUtil;
import com.eripe14.combatlog.bukkit.util.DurationUtil;
import com.eripe14.combatlog.combatlog.CombatLogManager;
import com.eripe14.combatlog.config.MessageConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class CombatLogManageTask implements Runnable {

    private final CombatLogManager combatLogManager;
    private final MessageConfig messageConfig;

    public CombatLogManageTask(CombatLogManager combatLogManager, MessageConfig messageConfig) {
        this.combatLogManager = combatLogManager;
        this.messageConfig = messageConfig;
    }

    @Override
    public void run() {
        for (UUID uuid : this.combatLogManager.getPlayersInCombat()) {
            Optional<Duration> leftTimeOptional = this.combatLogManager.getLeftTime(uuid);

            if (leftTimeOptional.isEmpty()) continue;

            Duration leftTime = leftTimeOptional.get();

            Player player = Bukkit.getPlayer(uuid);

            if (player == null) continue;

            if (leftTime.isZero() || leftTime.isNegative()) {
                this.combatLogManager.remove(uuid);

                player.sendMessage(ChatUtil.fixColor(this.messageConfig.unTagPlayer));

                continue;
            }

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatUtil.fixColor(this.messageConfig.combatLogDuration)
                    .replaceAll("%TIME%", DurationUtil.format(leftTime))));
        }
    }

}
