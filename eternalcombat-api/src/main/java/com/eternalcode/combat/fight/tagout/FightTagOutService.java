package com.eternalcode.combat.fight.tagout;

import java.time.Duration;
import java.util.UUID;

public interface FightTagOutService {

    Duration getRemainingTime(UUID player);
    boolean isTaggedOut(UUID player);

    void unTagOut(UUID player);

    void tagOut(UUID player, Duration duration);
}
