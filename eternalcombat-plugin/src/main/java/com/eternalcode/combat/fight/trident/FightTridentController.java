package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRiptideEvent;

import java.time.Duration;
import java.util.UUID;

public class FightTridentController implements Listener {

    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final FightTridentService fightTridentService;

    public FightTridentController(
        PluginConfig pluginConfig,
        NoticeService noticeService,
        FightManager fightManager,
        FightTridentService fightTridentService
    ) {
        this.pluginConfig = pluginConfig;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.fightTridentService = fightTridentService;
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            return;
        }

        this.handleTridentCooldown(player, playerId);

        if (this.pluginConfig.trident.tridentResetsTimerEnabled) {
            Duration combatTime = pluginConfig.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.TRIDENT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().distanceSquared(event.getTo()) == 0) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!player.isRiptiding()) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            if (!this.fightManager.isInCombat(playerId)) {
                return;
            }

            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.trident.tridentRiptideBlocked)
                .send();
        }
    }

    private void handleTridentCooldown(Player player, UUID playerId) {
        if (this.pluginConfig.trident.tridentRiptideDelay.isZero()) {
            return;
        }

        if (this.fightTridentService.hasDelay(playerId)) {
            Duration remainingDelay = this.fightTridentService.getRemainingDelay(playerId);

            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.trident.tridentRiptideOnCooldown)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();
            return;
        }

        this.fightTridentService.markDelay(playerId);
        int cooldownTicks = (int) (this.pluginConfig.trident.tridentRiptideDelay.toMillis() / 50);
        player.setCooldown(Material.TRIDENT, cooldownTicks);
    }
}
