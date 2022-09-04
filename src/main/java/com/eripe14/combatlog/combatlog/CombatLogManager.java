package com.eripe14.combatlog.combatlog;

import dev.rollczi.litecommands.argument.option.Opt;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CombatLogManager {

    private final Map<UUID, Instant> combatLogTimeMap = new HashMap<>( );
    private final Map<UUID, UUID> combatLogMap = new HashMap<>( );

    public boolean isInCombat(UUID player) {
        return this.combatLogTimeMap.containsKey(player);
    }

    public UUID getEnemy(UUID player) {
        return combatLogMap.get(player);
    }

    public void remove(UUID player) {
        combatLogTimeMap.remove(player);
        combatLogMap.remove(player);
    }

    public List<UUID> getPlayersInCombat() {
        return new ArrayList<>(this.combatLogTimeMap.keySet( ));
    }

    public void tag(UUID player, UUID enemy, Duration time) {
        if (isInCombat(player)) {
            this.remove(player);
        }

        combatLogTimeMap.put(player, Instant.now().plus(time));
        combatLogMap.put(player, enemy);
    }

    public Optional<Duration> getLeftTime(UUID player) {
        Instant endOfCombatLog = this.getCombatLogTimeMap().get(player);

        return Optional.ofNullable(Duration.between(Instant.now(), endOfCombatLog));
    }

    public Map<UUID, Instant> getCombatLogTimeMap() {
        return Collections.unmodifiableMap(this.combatLogTimeMap);
    }

    public Map<UUID, UUID> getCombatLogMap() {
        return Collections.unmodifiableMap(this.combatLogMap);
    }
}
