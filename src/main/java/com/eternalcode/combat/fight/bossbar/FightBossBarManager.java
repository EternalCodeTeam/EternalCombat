package com.eternalcode.combat.fight.bossbar;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FightBossBarManager {

    private final Map<UUID, FightBossBar> fightBossBars = new ConcurrentHashMap<>();

    public void add(UUID uuid, FightBossBar fightBossBar) {
        this.fightBossBars.put(uuid, fightBossBar);
    }

    public void remove(UUID uuid) {
        this.fightBossBars.remove(uuid);
    }

    public boolean hasFightBossBar(UUID uuid) {
        return this.fightBossBars.containsKey(uuid);
    }

    public Optional<FightBossBar> getFightBossBar(UUID uuid) {
        return Optional.ofNullable(this.fightBossBars.get(uuid));
    }
}
