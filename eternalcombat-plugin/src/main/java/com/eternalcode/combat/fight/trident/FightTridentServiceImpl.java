package com.eternalcode.combat.fight.trident;

import com.eternalcode.combat.util.delay.Delay;

import java.time.Duration;
import java.util.UUID;

public class FightTridentServiceImpl implements FightTridentService {

    private final FightTridentSettings delaySettings;
    private final Delay<UUID> delay;

    public FightTridentServiceImpl(FightTridentSettings delaySettings) {
        this.delaySettings = delaySettings;
        this.delay = Delay.withDefault(() -> this.delaySettings.tridentRiptideDelay);
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

