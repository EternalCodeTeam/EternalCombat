package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class PearlController implements Listener {

    private final PluginConfig pluginConfig;
    private final PearlService pearlService;
    private final NoticeService noticeService;

    public PearlController(
        PluginConfig pluginConfig,
        PearlService pearlService, NoticeService noticeService
    ) {
        this.pluginConfig = pluginConfig;
        this.pearlService = pearlService;
        this.noticeService = noticeService;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        UUID playerId = player.getUniqueId();

        if (this.pearlService.shouldCancelEvent(playerId)) {
            event.setCancelled(true);

            if (this.pluginConfig.pearl.pearlThrowDisabledDuringCombat) {
                this.noticeService.create()
                    .player(playerId)
                    .notice(this.pluginConfig.pearl.pearlThrowBlockedDuringCombat)
                    .send();
                return;
            }

            Duration remainingDelay = this.pearlService.getRemainingDelay(playerId);
            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.pearl.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();

        }

        this.pearlService.handleDelay(playerId);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlDamage(EntityDamageByEntityEvent event) {
        if (this.pluginConfig.pearl.pearlThrowDamageEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player) ||
            !(event.getDamager() instanceof EnderPearl) ||
            event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        event.setDamage(0.0);
    }

}
