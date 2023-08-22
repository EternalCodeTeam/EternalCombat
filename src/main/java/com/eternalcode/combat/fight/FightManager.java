package com.eternalcode.combat.fight;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FightManager {

    private final Map<UUID, FightTag> fights = new ConcurrentHashMap<>();

    public boolean isInCombat(UUID player) {
        if (!this.fights.containsKey(player)) {
            return false;
        }

        FightTag fightTag = this.fights.get(player);

        if (fightTag.isExpired()) {
            this.fights.remove(player);
            return false;
        }

        return true;
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

    public FightTag getTag(UUID target) {
        return this.fights.get(target);
    }

    public void untagAll() {
        this.fights.clear();
    }
}
