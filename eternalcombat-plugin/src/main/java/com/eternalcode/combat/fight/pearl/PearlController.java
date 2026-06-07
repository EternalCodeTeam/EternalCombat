package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
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
import org.bukkit.event.player.PlayerTeleportEvent;

public class PearlController implements Listener {

    private final PluginConfig pluginConfig;
    private final PearlService pearlService;
    private final NoticeService noticeService;
    private final FightManager fightManager;

    public PearlController(
        PluginConfig pluginConfig,
        PearlService pearlService, NoticeService noticeService, FightManager fightManager
    ) {
        this.pluginConfig = pluginConfig;
        this.pearlService = pearlService;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
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

        this.pearlService.handleDelay(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlDamage(EntityDamageByEntityEvent event) {
        if (this.pluginConfig.pearl.pearlThrowDamageEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player) ||
            !(event.getDamager() instanceof EnderPearl) ||
            event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }

        event.setDamage(0.0);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        if (!this.pluginConfig.pearl.pearlThrowDisabledDuringCombat) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        event.setCancelled(true);

        this.noticeService.create()
            .player(playerId)
            .notice(this.pluginConfig.pearl.pearlThrowBlockedDuringCombat)
            .send();
    }

}
