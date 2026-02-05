package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import java.time.Duration;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class FightPearlController implements Listener {

    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightPearlService fightPearlService;

    public FightPearlController(
        PluginConfig pluginConfig,
        NoticeService noticeService,
        FightManager fightManager,
        FightPearlService fightPearlService
    ) {
        this.pluginConfig = pluginConfig;
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

        if (this.pluginConfig.pearl.pearlThrowDisabledDuringCombat) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.pearl.pearlThrowBlockedDuringCombat)
                .send();
            return;
        }

        if (this.pluginConfig.pearl.pearlCooldownEnabled) {
            handlePearlCooldown(event, player, playerId);
        }

        if (this.pluginConfig.pearl.pearlResetsTimerEnabled) {
            Duration combatTime = this.pluginConfig.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.ENDER_PEARL);
        }
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

    private void handlePearlCooldown(ProjectileLaunchEvent event, Player player, UUID playerId) {
        if (this.pluginConfig.pearl.pearlThrowDelay.isZero()) {
            return;
        }

        if (this.fightPearlService.hasDelay(playerId)) {
            event.setCancelled(true);
            Duration remainingDelay = this.fightPearlService.getRemainingDelay(playerId);

            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.pearl.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();
            return;
        }

        this.fightPearlService.markDelay(playerId);
        int cooldownTicks = (int) (this.pluginConfig.pearl.pearlThrowDelay.toMillis() / 50);
        player.setCooldown(Material.ENDER_PEARL, cooldownTicks);
    }
}
