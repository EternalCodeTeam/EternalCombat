package com.eternalcode.combat.fight.pearl;

import java.time.Duration;

public interface FightPearlSettings {

    Duration pearlThrowDuration();

    boolean shouldBlockThrowingPearls();

    boolean shouldBlockThrowingPearlsWithDelay();
}
