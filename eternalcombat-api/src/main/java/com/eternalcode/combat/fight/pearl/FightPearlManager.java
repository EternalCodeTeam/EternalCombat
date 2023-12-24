package com.eternalcode.combat.fight.pearl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightPearlManager {

    private final FightPearlSettings pearlSettings;
    private final Cache<UUID, Instant> pearlDelays;

    public FightPearlManager(FightPearlSettings pearlSettings) {
        this.pearlSettings = pearlSettings;
        this.pearlDelays = Caffeine.newBuilder()
            .expireAfterWrite(pearlSettings.pearlThrowDelay)
            .build();
    }

    public void markDelay(UUID uuid) {
        this.pearlDelays.put(uuid, Instant.now().plus(this.pearlSettings.pearlThrowDelay));
    }

    public boolean hasDelay(UUID uuid) {
        return Instant.now().isBefore(this.getDelay(uuid));
    }

    public Duration getRemainingDelay(UUID uuid) {
        return Duration.between(Instant.now(), this.getDelay(uuid));
    }

    public Instant getDelay(UUID uuid) {
        return this.pearlDelays.asMap().getOrDefault(uuid, Instant.MIN);
    }
}
