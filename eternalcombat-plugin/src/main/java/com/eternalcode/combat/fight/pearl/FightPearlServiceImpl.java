package com.eternalcode.combat.fight.pearl;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;

public class FightPearlServiceImpl implements FightPearlService {

    private final Delay<UUID> pearlStartTimes;

    public FightPearlServiceImpl(PluginConfig config) {
        this.pearlStartTimes = Delay.withDefault(() -> config.pearl.pearlThrowDelay);
    }

    @Override
    public void markDelay(UUID uuid) {
        this.pearlStartTimes.markDelay(uuid);
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
