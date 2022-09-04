package com.eripe14.combatlog.combatlog;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CombatLogManager {

    private final Map<UUID, Instant> combatLogLeftTimeMap = new HashMap<>();
    private final Map<UUID, UUID> combatLogMap = new HashMap<>();

    public boolean isInCombat(UUID player) {
        return this.combatLogLeftTimeMap.containsKey(player);
    }

    public UUID getEnemy(UUID player) {
        return combatLogMap.get(player);
    }

    public void remove(UUID player) {
        combatLogLeftTimeMap.remove(player);
        combatLogMap.remove(player);
    }

    public List<UUID> getPlayersInCombat() {
        return new ArrayList<>(this.combatLogLeftTimeMap.keySet());
    }

    public void tag(UUID player, UUID enemy, Duration time) {
        if (isInCombat(player)) {
            this.remove(player);
        }

        combatLogLeftTimeMap.put(player, Instant.now().plus(time));
        combatLogMap.put(player, enemy);
    }

    public Optional<Duration> getLeftTime(UUID player) {
        Instant now = Instant.now();
        Instant endOfCombatLog = this.getCombatLogLeftTimeMap().get(player);
        Duration between = Duration.between(now, endOfCombatLog);

        return Optional.ofNullable(between);
    }

    public Map<UUID, Instant> getCombatLogLeftTimeMap() {
        return Collections.unmodifiableMap(this.combatLogLeftTimeMap);
    }

    public Map<UUID, UUID> getCombatLogMap() {
        return Collections.unmodifiableMap(this.combatLogMap);
    }
}
