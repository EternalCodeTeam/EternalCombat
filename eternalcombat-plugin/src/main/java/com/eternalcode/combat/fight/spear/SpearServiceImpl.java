package com.eternalcode.combat.fight.spear;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpearServiceImpl implements SpearService {

    private final SpearSettings settings;
    private final Map<UUID, Instant> cooldowns = new ConcurrentHashMap<>();

    public SpearServiceImpl(SpearSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean isOnCooldown(UUID uuid) {
        Instant expiration = this.cooldowns.get(uuid);

        if (expiration == null) {
            return false;
        }

        if (Instant.now().isAfter(expiration)) {
            this.cooldowns.remove(uuid);
            return false;
        }

        return true;
    }

    @Override
    public void saveCooldown(UUID uuid) {
        this.cooldowns.put(uuid, Instant.now().plus(this.settings.lungeCooldownDuration));
    }

    @Override
    public Duration getRemainingCooldown(UUID uuid) {
        Instant expiration = this.cooldowns.get(uuid);

        if (expiration == null) {
            return Duration.ZERO;
        }

        Duration remaining = Duration.between(Instant.now(), expiration);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }
}
