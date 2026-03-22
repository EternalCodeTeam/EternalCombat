package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.time.DurationFormatter;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.time.Duration;
import java.util.UUID;

public class FightPearlController implements Listener {

    private final PluginConfig config;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightPearlService fightPearlService;

    public FightPearlController(
        PluginConfig config,
        NoticeService noticeService,
        FightManager fightManager,
        FightPearlService fightPearlService
    ) {
        this.config = config;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.fightPearlService = fightPearlService;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPearlThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        if (this.config.pearl.pearlThrowDisabledDuringCombat) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.config.pearl.pearlThrowBlockedDuringCombat)
                .send();
            return;
        }

        if (this.config.pearl.pearlCooldownEnabled) {
            handlePearlCooldown(event, player, playerId);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlDamage(EntityDamageByEntityEvent event) {
        if (this.config.pearl.pearlThrowDamageEnabled) {
            return;
        }

        if (!(event.getEntity() instanceof Player) ||
            !(event.getDamager() instanceof EnderPearl) ||
            event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        event.setDamage(0.0);
    }

    private void handlePearlCooldown(ProjectileLaunchEvent event, Player player, UUID playerId) {
        if (this.config.pearl.pearlThrowDelay.isZero()) {
            return;
        }

        if (this.fightPearlService.hasDelay(playerId)) {
            event.setCancelled(true);
            Duration remainingDelay = this.fightPearlService.getRemainingDelay(playerId);

            this.noticeService.create()
                .player(playerId)
                .notice(this.config.pearl.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationFormatter.of(config.durationFormat).format(remainingDelay))
                .send();
            return;
        }

        this.fightPearlService.markDelay(playerId);
        int cooldownTicks = (int) (this.config.pearl.pearlThrowDelay.toMillis() / 50);
        player.setCooldown(Material.ENDER_PEARL, cooldownTicks);
    }
}
