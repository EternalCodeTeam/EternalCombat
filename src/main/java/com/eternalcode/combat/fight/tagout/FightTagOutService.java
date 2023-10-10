package com.eternalcode.combat.fight.tagout;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FightTagOutService {

    private Map<UUID, Instant> tagOutService = new HashMap<>();

    public void tagOut(UUID player, Duration duration) {
        Instant endTime = Instant.now().plus(duration);

        this.tagOutService.put(player, endTime);
    }

    public void unTagOut(UUID player) {
        this.tagOutService.remove(player);
    }

    public boolean isTaggedOut(UUID player) {
        if (!this.tagOutService.containsKey(player)) {
            return false;
        }

        Instant endTime = this.tagOutService.get(player);

        return Instant.now().isBefore(endTime);
    }

}
