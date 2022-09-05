package com.eripe14.combatlog.combatlog;

import java.time.Instant;
import java.util.UUID;

public class Combat {

    private final UUID uuid;

    private final UUID enemy;

    private final Instant endOfCombatLog;

    public Combat(UUID uuid, UUID enemy, Instant endOfCombatLog) {
        this.uuid = uuid;
        this.enemy = enemy;
        this.endOfCombatLog = endOfCombatLog;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getEnemy() {
        return this.enemy;
    }

    public Instant getEndOfCombatLog() {
        return this.endOfCombatLog;
    }
}
