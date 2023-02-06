package com.eternalcode.combat.combat;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final Map<UUID, Combat> combats = new HashMap<>();

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
            this.remove(player);
        }

        Instant now = Instant.now();
        Instant extendedTime = now.plus(time);

        Combat combat = new Combat(player, enemy, extendedTime);

        this.combats.put(player, combat);
    }

    public void tag(UUID enemy, Duration time) {
        if (isInCombat(enemy)) {
            this.remove(enemy);
        }

        Instant now = Instant.now();
        Instant extendedTime = now.plus(time);

        Combat combat = new Combat(enemy, enemy, extendedTime);

        this.combats.put(enemy, combat);
    }

    public Collection<Combat> getCombats() {
        return Collections.unmodifiableCollection(this.combats.values());
    }

}
