package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRiptideEvent;

import java.time.Duration;
import java.util.UUID;

public class TridentController implements Listener {

    private final PluginConfig pluginConfig;
    private final NoticeService noticeService;
    private final FightManager fightManager;
    private final TridentService tridentService;

    public TridentController(
        PluginConfig pluginConfig,
        NoticeService noticeService,
        FightManager fightManager,
        TridentService tridentService
    ) {
        this.pluginConfig = pluginConfig;
        this.noticeService = noticeService;
        this.fightManager = fightManager;
        this.tridentService = tridentService;
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        UUID uniqueId = player.getUniqueId();

        if (!this.fightManager.isInCombat(uniqueId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            return;
        }


        if (this.tridentService.hasDelay(uniqueId)) {
            Duration remainingDelay = this.tridentService.getRemainingDelay(uniqueId);

            this.noticeService.create()
                .player(uniqueId)
                .notice(this.pluginConfig.trident.tridentRiptideOnCooldown)
                .placeholder("{TIME}", DurationUtil.format(remainingDelay))
                .send();

            return;
        }

        this.tridentService.handleTridentDelay(player);

        if (this.pluginConfig.trident.riptideResetsTimerEnabled) {
            this.fightManager.tag(uniqueId, this.pluginConfig.settings.combatTimerDuration, CauseOfTag.TRIDENT);
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

        if (!this.fightManager.isInCombat(playerId)) {
            return;
        }

        if (this.pluginConfig.trident.tridentRiptideDisabledDuringCombat) {
            event.setCancelled(true);
            this.noticeService.create()
                .player(playerId)
                .notice(this.pluginConfig.trident.tridentRiptideBlocked)
                .send();
        }
    }
}
