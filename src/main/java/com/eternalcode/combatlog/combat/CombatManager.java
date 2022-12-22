package com.eternalcode.combatlog.combat;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {
    private final Map<UUID, Combat> combats = new ConcurrentHashMap<>();

    public boolean isInCombat(UUID player) {
        return this.combats.containsKey(player);
    }

    public UUID getEnemy(UUID player) {
        Combat combat = this.combats.get(player);
        return combat.getEnemy();
    }

    public void remove(UUID player) {
        this.combats.remove(player);
    }

    public void tag(UUID player, UUID enemy, Duration time) {
        if (isInCombat(player)) {
            remove(player);
        }

        Instant now = Instant.now();
        Instant extendedTime = now.plus(time);

        Combat combat = new Combat(player, enemy, extendedTime);

        this.combats.put(player, combat);
    }

    public Collection<Combat> getCombats() {
        return Collections.unmodifiableCollection(this.combats.values());
    }
}

