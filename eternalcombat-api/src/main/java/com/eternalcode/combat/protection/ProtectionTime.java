package com.eternalcode.combat.protection;

import java.time.Duration;
import java.time.Instant;

public class ProtectionTime {

    private final Duration remainingTime;
    private final Instant initialTime;

    ProtectionTime(Duration remainingTime, Instant now) {
        this.remainingTime = remainingTime;
        this.initialTime = now;
    }

    public Duration getRemainingTime() {
        return this.remainingTime;
    }

    public Instant getInitialTime() {
        return this.initialTime;
    }

}
