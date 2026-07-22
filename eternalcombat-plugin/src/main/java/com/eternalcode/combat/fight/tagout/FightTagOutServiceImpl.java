package com.eternalcode.combat.fight.tagout;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class FightTagOutServiceImpl implements FightTagOutService {

    // Self-expiring cache so entries are evicted at their end time instead of
    // accumulating for every player that has ever tagged out.
    private final Cache<UUID, Instant> tagOuts = Caffeine.newBuilder()
        .expireAfter(new Expiry<UUID, Instant>() {
            @Override
            public long expireAfterCreate(UUID key, Instant endTime, long currentTime) {
                return Math.max(0, Duration.between(Instant.now(), endTime).toNanos());
            }

            @Override
            public long expireAfterUpdate(UUID key, Instant endTime, long currentTime, long currentDuration) {
                return this.expireAfterCreate(key, endTime, currentTime);
            }

            @Override
            public long expireAfterRead(UUID key, Instant endTime, long currentTime, long currentDuration) {
                return currentDuration;
            }
        })
        .build();

    @Override
    public void tagOut(UUID player, Duration duration) {
        Instant endTime = Instant.now().plus(duration);

        this.tagOuts.put(player, endTime);
    }

    @Override
    public void unTagOut(UUID player) {
        this.tagOuts.invalidate(player);
    }

    @Override
    public boolean isTaggedOut(UUID player) {
        Instant endTime = this.tagOuts.getIfPresent(player);

        if (endTime == null) {
            return false;
        }
        Instant now = Instant.now();

        return now.isBefore(endTime);
    }

}
