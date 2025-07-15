package com.eternalcode.combat.fight.pearl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightPearlServiceImpl implements FightPearlService {

    private final FightPearlSettings pearlSettings;
    private final Cache<UUID, Instant> pearlStartTimes;

    public FightPearlServiceImpl(FightPearlSettings pearlSettings) {
        this.pearlSettings = pearlSettings;
        this.pearlStartTimes = Caffeine.newBuilder()
            .expireAfterWrite(pearlSettings.pearlThrowDelay)
            .build();
    }

    @Override
    public void markDelay(UUID uuid) {
        this.pearlStartTimes.put(uuid, Instant.now());
    }

    @Override
    public boolean hasDelay(UUID uuid) {
        return this.pearlStartTimes.getIfPresent(uuid) != null;
    }

    @Override
    public Duration getRemainingDelay(UUID uuid) {
        Instant startTime = this.pearlStartTimes.getIfPresent(uuid);
        if (startTime == null) {
            return Duration.ZERO;
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        Duration remaining = this.pearlSettings.pearlThrowDelay.minus(elapsed);

        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Override
    public Instant getDelay(UUID uuid) {
        Instant startTime = this.pearlStartTimes.getIfPresent(uuid);
        return startTime != null ? startTime.plus(this.pearlSettings.pearlThrowDelay) : Instant.MIN;
    }
}
