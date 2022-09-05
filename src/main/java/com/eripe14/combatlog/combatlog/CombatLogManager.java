package com.eripe14.combatlog.combatlog;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CombatLogManager {

    private final Map<UUID, Combat> combatLogMap = new HashMap<>();

    public boolean isInCombat(UUID player) {
        return this.combatLogMap.containsKey(player);
    }

    public UUID getEnemy(UUID player) {
        Combat combat = this.combatLogMap.get(player);

        return combat.getEnemy();
    }

    public void remove(UUID player) {
        combatLogMap.remove(player);
    }

    public List<UUID> getPlayersInCombat() {
        return new ArrayList<>(this.combatLogMap.keySet());
    }

    public void tag(UUID player, UUID enemy, Duration time) {
        if (isInCombat(player)) {
            this.remove(player);
        }

        Combat combat = new Combat(player, enemy, Instant.now().plus(time));

        combatLogMap.put(player, combat);
    }

    public Optional<Duration> getLeftTime(UUID player) {
        Combat combat = this.getCombatLogMap().get(player);

        Instant now = Instant.now();
        Instant endOfCombatLog = combat.getEndOfCombatLog();

        Duration between = Duration.between(now, endOfCombatLog);

        return Optional.ofNullable(between);
    }

    public Map<UUID, Combat> getCombatLogMap() {
        return Collections.unmodifiableMap(this.combatLogMap);
    }

}
