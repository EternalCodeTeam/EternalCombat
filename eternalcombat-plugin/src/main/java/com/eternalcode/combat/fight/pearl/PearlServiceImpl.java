package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.fight.FightManager;
import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.notification.NoticeService;
import com.eternalcode.combat.util.DurationUtil;
import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;

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
    public void handleDelay(UUID playerId) {
        if (this.pluginConfig.pearl.pearlResetsTimer) {
            Duration combatTime = this.pluginConfig.settings.combatTimerDuration;
            this.fightManager.tag(playerId, combatTime, CauseOfTag.ENDER_PEARL);
        }

        if (!this.pluginConfig.pearl.pearlThrowDelay.isZero()) {
            this.pearlStartTimes.markDelay(playerId);
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
