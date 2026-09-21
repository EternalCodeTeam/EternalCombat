package com.eternalcode.combat.fight.spear;

import java.time.Duration;
import java.util.UUID;

public interface SpearService {
    boolean isOnCooldown(UUID player);
    void saveCooldown(UUID player);
    Duration getRemainingCooldown(UUID player);
    void removeCooldown(UUID player);
}
