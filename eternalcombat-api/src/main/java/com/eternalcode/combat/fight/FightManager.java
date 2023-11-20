package com.eternalcode.combat.fight;

import com.eternalcode.combat.fight.event.CauseOfTag;
import com.eternalcode.combat.fight.event.CauseOfUnTag;
import com.eternalcode.combat.fight.event.FightTagEvent;
import com.eternalcode.combat.fight.event.FightUntagEvent;
import com.eternalcode.combat.event.EventCaller;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FightManager {

    private final Map<UUID, FightTag> fights = new ConcurrentHashMap<>();
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

    public FightUntagEvent untag(UUID player, CauseOfUnTag causeOfUnTag) {
        FightUntagEvent event = this.eventCaller.publishEvent(new FightUntagEvent(player, causeOfUnTag));
        if (event.isCancelled()) {
            return event;
        }

        this.fights.remove(player);
        return event;
    }

    public FightTagEvent tag(UUID target, Duration delay, CauseOfTag causeOfTag) {
        FightTagEvent event = this.eventCaller.publishEvent(new FightTagEvent(target, causeOfTag));

        if (event.isCancelled()) {
            return event;
        }
        Instant now = Instant.now();
        Instant endOfCombatLog = now.plus(delay);

        FightTag fightTag = new FightTag(target, endOfCombatLog);

        this.fights.put(target, fightTag);
        return event;
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
