package com.eternalcode.combat.fight;

import com.eternalcode.combat.event.EventCaller;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

public class FightManager {

    private final Map<UUID, FightTag> fights = new HashMap<>();
    private final EventCaller eventCaller;

    public FightManager(EventCaller eventCaller) {
        this.eventCaller = eventCaller;
    }

    public boolean isInCombat(UUID player) {
        if (!this.fights.containsKey(player)) {
            return false;
        }

        FightTag fightTag = this.fights.get(player);

        return !fightTag.isExpired();
    }

    public void untag(UUID player) {
        this.eventCaller.callEvent(new FightUntagEvent(player));
        
        this.fights.remove(player);
    }

    public void tag(UUID target, Duration delay) {
        this.eventCaller.callEvent(new FightTagEvent(target));
        
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
