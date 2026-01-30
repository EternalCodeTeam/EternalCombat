package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerRiptideEvent;

import java.time.Duration;
import java.util.UUID;

public class FightTridentController implements Listener {

    private final FightTridentSettings settings;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightTridentService fightTridentService;
    private final PluginConfig config;

    public FightTridentController(
        FightTridentSettings settings,
        NoticeService noticeService,
        FightManager fightManager,
        FightTridentService fightTridentService,
        PluginConfig config
    ) {
        this.settings = settings;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.fightTridentService = fightTridentService;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTridentThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        if (!(trident.getShooter() instanceof Player player)) {
            return;
        }

        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        if (this.settings.tridentThrowDisabledDuringCombat) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.settings.tridentThrowBlockedDuringCombat)
                .send();
            return;
        }

        if (this.settings.tridentCooldownEnabled) {
            this.handleTridentCooldown(event, player, playerId);
            if (event.isCancelled()) {
                return;
            }
        }

        if (this.settings.tridentResetsTimer) {
            Duration combatTime = this.config.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.NON_PLAYER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTridentRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        Cancellable cancellable = event instanceof Cancellable ? (Cancellable) event : null;

        if (this.settings.tridentThrowDisabledDuringCombat) {
            if (cancellable != null) {
                cancellable.setCancelled(true);
            }
            this.noticeService.create()
                .player(playerId)
                .notice(this.settings.tridentThrowBlockedDuringCombat)
                .send();
            return;
        }

        if (this.settings.tridentCooldownEnabled) {
            this.handleTridentCooldown(cancellable, player, playerId);
            if (cancellable != null && cancellable.isCancelled()) {
                return;
            }
        }

        if (this.settings.tridentResetsTimer) {
            Duration combatTime = this.config.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.NON_PLAYER);
        }
    }

    private void handleTridentCooldown(Cancellable cancellable, Player player, UUID playerId) {
        if (this.settings.tridentThrowDelay.isZero()) {
            return;
        }

        if (this.fightTridentService.hasDelay(playerId)) {
            if (cancellable != null) {
                cancellable.setCancelled(true);
            }
            Duration remainingDelay = this.fightTridentService.getRemainingDelay(playerId);

            this.noticeService.create()
                .player(playerId)
                .notice(this.settings.tridentThrowBlockedDelayDuringCombat)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();
            return;
        }

        this.fightTridentService.markDelay(playerId);
        int cooldownTicks = (int) (this.settings.tridentThrowDelay.toMillis() / 50);
        player.setCooldown(Material.TRIDENT, cooldownTicks);
    }
}
