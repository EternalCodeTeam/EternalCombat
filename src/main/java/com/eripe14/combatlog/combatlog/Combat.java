package com.eripe14.combatlog.combatlog;

import java.time.Instant;
import java.util.UUID;

public class Combat {

    private final UUID enemy;

    private final Instant endOfCombatLog;

    public Combat(UUID enemy, Instant endOfCombatLog) {
        this.enemy = enemy;
        this.endOfCombatLog = endOfCombatLog;
    }

    public UUID getEnemy() {
        return this.enemy;
    }

    public Instant getEndOfCombatLog() {
        return this.endOfCombatLog;
    }
}
