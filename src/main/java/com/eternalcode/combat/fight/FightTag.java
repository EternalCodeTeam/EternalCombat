package com.eternalcode.combat.fight;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightTag {

    private final UUID taggedPlayer;

    private final Instant endOfCombatLog;
    private Instant endOfPearlDelay;

    FightTag(UUID personToAddCombat, Instant endOfCombatLog) {
        this.taggedPlayer = personToAddCombat;
        this.endOfCombatLog = endOfCombatLog;
        this.endOfPearlDelay = Instant.MIN;
    }

    public UUID getTaggedPlayer() {
        return this.taggedPlayer;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.endOfCombatLog);
    }

    public Duration getRemainingDuration() {
        Duration between = Duration.between(Instant.now(), this.endOfCombatLog);

        if (between.isNegative()) {
            return Duration.ZERO;
        }

        return between;
    }

    public void setEndOfPearlDelay(Instant endOfPearlDelay) {
        this.endOfPearlDelay = endOfPearlDelay;
    }

    public boolean isPearlDelayExpired() {
        return Instant.now().isAfter(this.endOfPearlDelay);
    }

    public Duration getRemainingPearlDelay() {
        Duration between = Duration.between(Instant.now(), this.endOfPearlDelay);

        if (between.isNegative()) {
            return Duration.ZERO;
        }

        return between;
    }
}
