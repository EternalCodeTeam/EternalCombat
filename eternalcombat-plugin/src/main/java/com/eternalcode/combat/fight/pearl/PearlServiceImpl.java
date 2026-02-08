package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PearlServiceImpl implements PearlService {

    private final FightManager fightManager;
    private final PluginConfig pluginConfig;

    private final Delay<UUID> pearlStartTimes;

    public PearlServiceImpl(FightManager fightManager, PluginConfig pluginConfig) {
        this.fightManager = fightManager;
        this.pluginConfig = pluginConfig;

        this.pearlStartTimes = Delay.withDefault(() -> pluginConfig.pearl.pearlThrowDelay);
    }

    @Override
    public boolean shouldCancelEvent(UUID playerId) {
        if (fightManager.isInCombat(playerId)) {
            if (this.pluginConfig.pearl.pearlCooldownEnabled) {
                return this.pearlStartTimes.hasDelay(playerId);
            }

            return pluginConfig.pearl.pearlThrowDisabledDuringCombat;
        }

        return false;
    }

    @Override
    public void handleDelay(Player player) {
        UUID uniqueId = player.getUniqueId();

        if (this.hasDelay(uniqueId)) {
            return;
        }

        if (this.fightManager.isInCombat(uniqueId)) {
            if (this.pluginConfig.pearl.pearlResetsTimer) {
                Duration combatTime = this.pluginConfig.settings.combatTimerDuration;
                this.fightManager.tag(uniqueId, combatTime, CauseOfTag.ENDER_PEARL);
            }

            if (this.pluginConfig.pearl.pearlCooldownEnabled && !this.pluginConfig.pearl.pearlThrowDelay.isZero()) {
                    this.pearlStartTimes.markDelay(uniqueId);
                }

        }
    }

    @Override
    public boolean hasDelay(UUID uuid) {
        return this.pearlStartTimes.hasDelay(uuid);
    }

    @Override
    public Duration getRemainingDelay(UUID uuid) {
        return this.pearlStartTimes.getRemaining(uuid);
    }

}
