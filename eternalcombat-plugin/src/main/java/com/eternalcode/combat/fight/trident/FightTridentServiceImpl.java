package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.config.implementation.PluginConfig;
import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;

public class FightTridentServiceImpl implements FightTridentService {

    private final Delay<UUID> delay;

    public FightTridentServiceImpl(PluginConfig config) {
        this.delay = Delay.withDefault(() -> config.trident.tridentRiptideDelay);
    }

    @Override
    public void markDelay(UUID uuid) {
        this.delay.markDelay(uuid);
    }

    @Override
    public boolean hasDelay(UUID uuid) {
        return this.delay.hasDelay(uuid);
    }

    @Override
    public Duration getRemainingDelay(UUID uuid) {
        return this.delay.getRemaining(uuid);
    }
}

