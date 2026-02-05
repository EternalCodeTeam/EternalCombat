package com.eternalcode.combat.fight.trident;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public interface FightTridentService {

    /**
     * returns remaining delay for the player to use trident again
     * @param uuid unique id of the player
     * @return remaining duration left to use trident again by the player
     */
    Duration getRemainingDelay(UUID uuid);

    /**
     * checks if player still has delay left to use trident
     * @param uuid unique id of the player
     * @return true if user still has cooldown left to use trident
     */
    boolean hasDelay(UUID uuid);

    /**
     * marks start of the delay for the user
     * @param uuid unique id of the player
     */
    void markDelay(UUID uuid);
}
