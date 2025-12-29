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

    private final FightPearlSettings settings;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightPearlService fightPearlService;
    private final PluginConfig config;

    public FightPearlController(
        FightPearlSettings settings,
        NoticeService noticeService,
        FightManager fightManager,
        FightPearlService fightPearlService,
        PluginConfig config
    ) {
        this.settings = settings;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.fightPearlService = fightPearlService;
        this.config = config;
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

        if (this.settings.pearlThrowDisabledDuringCombat) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.settings.pearlThrowBlockedDuringCombat)
                .send();
            return;
        }

        if (this.settings.pearlCooldownEnabled) {
            handlePearlCooldown(event, player, playerId);
        }

        if (this.settings.pearlResetsTimer) {
            Duration combatTime = this.config.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.NON_PLAYER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPearlDamage(EntityDamageByEntityEvent event) {
        if (this.settings.pearlThrowDamageEnabled) {
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
        if (this.settings.pearlThrowDelay.isZero()) {
            return;
        }

        if (this.fightPearlService.hasDelay(playerId)) {
            event.setCancelled(true);
            Duration remainingDelay = this.fightPearlService.getRemainingDelay(playerId);

            this.noticeService.create()
                .player(playerId)
                .notice(this.settings.pearlThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();
            return;
        }

        this.fightPearlService.markDelay(playerId);
        int cooldownTicks = (int) (this.settings.pearlThrowDelay.toMillis() / 50);
        player.setCooldown(Material.ENDER_PEARL, cooldownTicks);
    }
}
