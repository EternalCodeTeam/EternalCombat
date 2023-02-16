package com.eternalcode.combat.fight;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightTag {

    private final UUID taggedPlayer;

    private final Instant endOfCombatLog;

    FightTag(UUID enemy, Instant endOfCombatLog) {
        this.taggedPlayer = enemy;
        this.endOfCombatLog = endOfCombatLog;
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

}
