package com.eternalcode.combat.fight.spear;

import java.time.Duration;
import java.util.UUID;

public interface SpearService {
    boolean isOnCooldown(UUID uuid);
    void saveCooldown(UUID uuid);
    Duration getRemainingCooldown(UUID uuid);
}
