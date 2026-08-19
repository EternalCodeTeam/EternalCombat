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
        return !this.getRemainingCooldown(uuid).isZero();
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

        Instant now = Instant.now();
        if (now.isAfter(expiration)) {
            this.cooldowns.remove(uuid);
            return Duration.ZERO;
        }

        return Duration.between(now, expiration);
    }

    @Override
    public void removeCooldown(UUID uuid) {
        this.cooldowns.remove(uuid);
    }
}
