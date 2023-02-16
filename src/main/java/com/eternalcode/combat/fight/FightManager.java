package com.eternalcode.combat.fight;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FightManager {

    private final Map<UUID, FightTag> fights = new ConcurrentHashMap<>();

    public boolean isInCombat(UUID player) {
        return this.getTag(player).isPresent();
    }

    public Optional<FightTag> getTag(UUID player) {
        FightTag fightTag = this.fights.get(player);

        if (fightTag == null) {
            return Optional.empty();
        }

        if (fightTag.isExpired()) {
            return Optional.empty();
        }

        return Optional.of(fightTag);
    }

    public UUID getEnemy(UUID player) {
        FightTag fightTag = this.fights.get(player);

        return fightTag.getTaggedPlayer();
    }

    public void untag(UUID player) {
        this.fights.remove(player);
    }

    public void tag(UUID target, Duration delay) {
        Instant now = Instant.now();
        Instant endOfCombatLog = now.plus(delay);

        FightTag fightTag = new FightTag(target, endOfCombatLog);

        this.fights.put(target, fightTag);
    }

    public Collection<FightTag> getFights() {
        return Collections.unmodifiableCollection(this.fights.values());
    }

    public void untagAll() {
        this.fights.clear();
    }
}
