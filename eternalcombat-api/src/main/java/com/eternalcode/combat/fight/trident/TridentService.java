package com.eternalcode.combat.fight.trident;

import java.time.Duration;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface TridentService {

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

    /**
     * handles the trident cooldown for the player, should be called when player uses riptide in combat
     * @param player the player who used riptide in combat needed to apply cooldown to item
     */
    void handleTridentDelay(Player player);

}
