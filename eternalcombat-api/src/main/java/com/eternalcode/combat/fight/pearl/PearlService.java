package com.eternalcode.combat.fight.pearl;

import java.time.Duration;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface PearlService {

    boolean shouldCancelEvent(UUID playerId);

    Duration getRemainingDelay(UUID uuid);

    boolean hasDelay(UUID uuid);

    void handleDelay(Player uuid);
}
