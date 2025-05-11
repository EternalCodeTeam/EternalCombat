package com.eternalcode.combat.fight.tagout;

import java.time.Duration;
import java.util.UUID;

public interface FightTagOutService {

    boolean isTaggedOut(UUID player);

    void unTagOut(UUID player);

    void tagOut(UUID player, Duration duration);

    /**
     * Gets the remaining duration of tag immunity for a player.
     *
     * @param player The UUID of the player
     * @return The remaining duration of tag immunity, or Duration.ZERO if the player is not tagged out
     */
    Duration getRemainingTime(UUID player);
}
