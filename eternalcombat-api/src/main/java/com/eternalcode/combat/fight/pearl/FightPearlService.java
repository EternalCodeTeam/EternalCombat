package com.eternalcode.combat.fight.pearl;

import java.time.Duration;
import java.util.UUID;

public interface FightPearlService {

    Duration getRemainingDelay(UUID uuid);

    boolean hasDelay(UUID uuid);

    void markDelay(UUID uuid);
}
