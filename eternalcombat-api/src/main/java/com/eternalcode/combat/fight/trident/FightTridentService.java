package com.eternalcode.combat.fight.trident;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface FightTridentService {

    Instant getDelay(UUID uuid);

    Duration getRemainingDelay(UUID uuid);

    boolean hasDelay(UUID uuid);

    void markDelay(UUID uuid);
}
