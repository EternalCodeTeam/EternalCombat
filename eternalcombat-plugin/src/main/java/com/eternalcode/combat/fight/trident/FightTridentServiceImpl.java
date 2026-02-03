package com.eternalcode.combat.fight.trident;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightTridentServiceImpl implements FightTridentService {
    private final FightTridentSettings tridentSettings;
    private final Cache<UUID, Instant> tridentStartTimes;

    public FightTridentServiceImpl(FightTridentSettings tridentSettings) {
        this.tridentSettings = tridentSettings;
        this.tridentStartTimes = Caffeine.newBuilder()
            .expireAfterWrite(tridentSettings.tridentRiptideDelay)
            .build();
    }

    @Override
    public void markDelay(UUID uuid) {
        this.tridentStartTimes.put(uuid, Instant.now());
    }

    @Override
    public boolean hasDelay(UUID uuid) {
        return this.tridentStartTimes.getIfPresent(uuid) != null;
    }

    @Override
    public Duration getRemainingDelay(UUID uuid) {
        Instant startTime = this.tridentStartTimes.getIfPresent(uuid);
        if (startTime == null) {
            return Duration.ZERO;
        }

        Duration elapsed = Duration.between(startTime, Instant.now());
        Duration remaining = this.tridentSettings.tridentRiptideDelay.minus(elapsed);

        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Override
    public Instant getDelay(UUID uuid) {
        Instant startTime = this.tridentStartTimes.getIfPresent(uuid);
        return startTime != null ? startTime.plus(this.tridentSettings.tridentRiptideDelay) : Instant.MIN;
    }
}

