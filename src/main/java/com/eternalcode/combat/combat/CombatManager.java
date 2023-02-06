package com.eternalcode.combat.combat;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final Map<UUID, CombatTag> combats = new ConcurrentHashMap<>();

    public boolean isInCombat(UUID player) {
        return this.getTag(player).isPresent();
    }

    public Optional<CombatTag> getTag(UUID player) {
        CombatTag combatTag = this.combats.get(player);

        if (combatTag == null) {
            return Optional.empty();
        }

        if (combatTag.isExpired()) {
            return Optional.empty();
        }

        return Optional.of(combatTag);
    }

    public UUID getEnemy(UUID player) {
        CombatTag combatTag = this.combats.get(player);

        return combatTag.getTaggedPlayer();
    }

    public void untag(UUID player) {
        this.combats.remove(player);
    }

    public void tag(UUID target, Duration delay) {
        Instant now = Instant.now();
        Instant endOfCombatLog = now.plus(delay);

        CombatTag combatTag = new CombatTag(target, endOfCombatLog);

        this.combats.put(target, combatTag);
    }

    public Collection<CombatTag> getCombats() {
        return Collections.unmodifiableCollection(this.combats.values());
    }

    public void untagAll() {
        this.combats.clear();
    }
}
